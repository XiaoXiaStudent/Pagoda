package org.javaboy.pagoda.media.service.impl;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.utils.SecurityUtils;
import org.javaboy.pagoda.media.entity.FileDirectory;
import org.javaboy.pagoda.media.entity.MediaFiles;
import org.javaboy.pagoda.media.mapper.MediaFilesMapper;
import org.javaboy.pagoda.media.service.IFileDirectoryService;
import org.javaboy.pagoda.media.service.IMediaFilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>
 * 媒资信息 服务实现类
 * </p>
 *
 * @author javaboy
 * @since 2023-05-17
 */
@Service
public class MediaFilesServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements IMediaFilesService {

        @Resource
        IFileDirectoryService fileDirectoryService;

        @Resource
        IMediaFilesService proxy;
        @Resource
        MinioClient minioClient;

        String bucketName = "photo-fruits";

        @Override
        public AjaxResult getFilesInDirectory(Long directoryId) {

                List<MediaFiles> mediaFiles = lambdaQuery().eq(MediaFiles::getDirectoryId, directoryId).list();

                return mediaFiles != null ? AjaxResult.success(mediaFiles) : AjaxResult.error("目录下无数据");
        }

        @Override
        public AjaxResult upload(byte[] bytes, MediaFiles mediaFiles) {
                //这里我需要判断是否为大文件上传如果是的话就采用分片上传的逻辑 如果不是正常上传

                MediaFiles mediaFiles1 = addMediaInMinio(bytes, mediaFiles);
                storeMediaFileDetails(bytes, mediaFiles1);

                return AjaxResult.success("上传成功");
        }

        @Override
        public AjaxResult uploadchunk(byte[] bytes, int chunk, String fileMd5, String contentType) {

                try {
                        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);

                        String object = chunkFileFolderPath + chunk;

                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

                        PutObjectArgs objectArgs = PutObjectArgs.builder()
                                .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                                .contentType(contentType)
                                .bucket(bucketName)
                                .object(object)
                                .build();

                        minioClient.putObject(objectArgs);

                        byteArrayInputStream.close();
                        return AjaxResult.success("上传分块成功");
                } catch (Exception e) {

                        throw new RuntimeException(e);
                }
        }

        @Override
        public AjaxResult checkChunk(String fileMd5, int chunk) {
                String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);

                String objectPath = chunkFileFolderPath + fileMd5 + chunk;

                GetObjectArgs getObjectArgs = GetObjectArgs.builder().object(objectPath).bucket(bucketName).build();
                InputStream object;
                try {

                        object = minioClient.getObject(getObjectArgs);
                } catch (Exception e) {
                        return AjaxResult.success(false);
                }

                if (object == null) {
                        return AjaxResult.success(true);
                }
                return AjaxResult.success(false);

        }

        @Override
        public AjaxResult mergechunks(String fileMd5, String fileName, int chunkTotal, Long directoryId) {

                File[] files = getChunkStatus(fileMd5, chunkTotal, bucketName);

                String extension = fileName.substring(fileName.lastIndexOf("."));

                File mergeFile = null;
                //将分块文件合并
                try {

                        mergeFile = File.createTempFile("mergeFile", extension);

                        byte[] bytes = new byte[1024];

                        RandomAccessFile file_write = new RandomAccessFile(mergeFile, "rw");

                        for (File file : files) {
                                RandomAccessFile file_read = new RandomAccessFile(file, "r");

                                int len = -1;

                                while ((len = file_read.read(bytes)) != -1) {

                                        file_write.write(bytes, 0, len);
                                }

                                file_read.close();
                        }

                        file_write.close();

                        String sourceMd5 = fileMd5;

                        FileInputStream mergefileInputStream = new FileInputStream(mergeFile);

                        String mergeMd5 = org.springframework.util.DigestUtils.md5DigestAsHex(mergefileInputStream);

                        if (!sourceMd5.equals(mergeMd5)) {
                                return AjaxResult.error("文件合并失败");
                        }

                        byte[] mergeFileBytes = Files.readAllBytes(mergeFile.toPath());

                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mergeFileBytes);
                        MediaFiles mediaFiles = new MediaFiles();

                        mediaFiles.setDirectoryId(directoryId);
                        String objectName = getObjectName(mediaFiles);


                        String url = minioClient.getObjectUrl(bucketName,objectName);
                        mediaFiles.setFilename(fileName);
                        mediaFiles.setFileId(mergeMd5);
                        mediaFiles.setFilePath(objectName);
                        mediaFiles.setUrl(url);
                        mediaFiles.setBucket(bucketName);
                        mediaFiles.setUsername(SecurityUtils.getUsername());


                        PutObjectArgs objectArgs = PutObjectArgs.builder()
                                .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                                .contentType(extension)
                                .bucket(bucketName)
                                .object(objectName)
                                .build();

                        minioClient.putObject(objectArgs);

                        byteArrayInputStream.close();

                        storeMediaFileDetails(mergeFileBytes, mediaFiles);



                } catch (Exception e) {

                        AjaxResult.error("分块文件合并失败");
                }


                return AjaxResult.success("获取文件成功");
        }

        private File[] getChunkStatus(String fileMd5, int chunkTotal, String bucket) {
                String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);

                File[] chunkfiles = new File[chunkTotal];

                for (int i = 0; i < chunkTotal; i++) {

                        String chunkpath = chunkFileFolderPath  + i;

                        File file = null;

                        try {
                                file = File.createTempFile("chunk", null);

                        } catch (IOException e) {
                                AjaxResult.error("创建临时文件出错");
                        }

                        try {
                                GetObjectArgs args = GetObjectArgs.builder().object(chunkpath).bucket(bucket).build();

                                InputStream object = minioClient.getObject(args);
                                FileOutputStream fileOutputStream = new FileOutputStream(file);

                                IOUtils.copy(object, fileOutputStream);

                                chunkfiles[i] = file;

                                object.close();

                                fileOutputStream.close();

                        } catch (Exception e) {
                                AjaxResult.error("查询分块文件出错");

                        }

                }



                return chunkfiles;
        }

        private String getChunkFileFolderPath(String fileMd5) {
                return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
        }

        @Transactional
        public void storeMediaFileDetails(byte[] bytes, MediaFiles mediaFiles) {

                try {

                        mediaFiles.setFileId(DigestUtils.md5Hex(bytes));
                        mediaFiles.setCreateDate(LocalDateTime.now());
                        mediaFiles.setChangeDate(LocalDateTime.now());
                        mediaFiles.setStatus("1");

                        // Store the media file details into the database
                        proxy.save(mediaFiles);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        private MediaFiles addMediaInMinio(byte[] bytes, MediaFiles mediaFiles) {
                try {

                        //加密文件名
                        String fileId = DigestUtils.md5Hex(bytes);

                        //获取object名字
                        String objectNamePre = getObjectName(mediaFiles);
                        String objectName = objectNamePre + fileId;

                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

                        PutObjectArgs objectArgs = PutObjectArgs.builder()
                                .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                                .contentType(mediaFiles.getFileType())
                                .bucket(bucketName)
                                .object(objectName)
                                .build();

                        minioClient.putObject(objectArgs);

                        byteArrayInputStream.close();

                        String url = minioClient.getObjectUrl(bucketName, objectName);

                        mediaFiles.setUrl(url);

                        mediaFiles.setFilePath(objectName);
                        mediaFiles.setBucket(bucketName);

                        return mediaFiles;
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }

        }

        private String getObjectName(MediaFiles mediaFiles) {
                //获取要存储的object路径
                FileDirectory fileDirectory = fileDirectoryService.getById(mediaFiles.getDirectoryId());
                String ancestors = fileDirectory.getAncestors();

                // 分割祖先ID
                String[] ancestorIds = ancestors.split(",");

                // 查询祖先名字并拼接路径
                StringBuilder pathInMinio = new StringBuilder();
                for (String ancestorId : ancestorIds) {
                        FileDirectory ancestorDirectory = fileDirectoryService.getById(Long.parseLong(ancestorId));
                        pathInMinio.append(ancestorDirectory.getFileName()).append("/");
                }

                // 添加当前文件夹名字
                pathInMinio.append(fileDirectory.getFileName()).append("/");

                // Adding the current date in the format yyyyMMdd
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate localDate = LocalDate.now();
                pathInMinio.append(dtf.format(localDate)).append("/");

                // 最后，路径应该是这样的： "ancestor1/ancestor2/.../currentDirectory/"
                return pathInMinio.toString();
        }
}
