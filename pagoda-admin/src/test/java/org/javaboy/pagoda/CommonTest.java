package org.javaboy.pagoda;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.javaboy.pagoda.common.core.domain.model.LoginUser;
import org.javaboy.pagoda.common.utils.uuid.SnowflakeIdWorker;
import org.javaboy.pagoda.common.utils.uuid.UUID;
import org.javaboy.pagoda.media.entity.FileDirectory;
import org.javaboy.pagoda.media.entity.MediaFiles;
import org.javaboy.pagoda.media.mapper.FileDirectoryMapper;
import org.javaboy.pagoda.media.service.IMediaFilesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@SpringBootTest
public class CommonTest {

        @Resource
        SnowflakeIdWorker snowflakeIdWorker;

        @Resource
        ThreadPoolTaskExecutor threadPoolTaskExecutor;
        @Resource
        RedisTemplate redisTemplate;

        @Resource
        MinioClient minioClient;

        @Resource
        IMediaFilesService mediaFilesService;

        @Resource
        ObjectMapper objectMapper;

        @Resource
        FileDirectoryMapper fileDirectoryMapper;

        @Test
        void zz9() {

                System.out.println("snowflakeIdWorker = ");
        }

        @Test
        void dirctory2() {
                // 获取所有文件夹
                List<FileDirectory> allDirectories = fileDirectoryMapper.selectList(null);

                // 构建根节点
                List<FileDirectory> rootDirectories = allDirectories.stream().filter(d -> d.getParentId() == 0).collect(Collectors.toList());

                // 递归构建每个根节点的子文件夹列表
                rootDirectories.forEach(root -> {
                        buildChildren(root, allDirectories);
                });

                rootDirectories.forEach(r -> System.out.println("r = " + r));
        }

        @Test
        void dirctory() {
                // 获取所有文件夹
                List<FileDirectory> allDirectories = fileDirectoryMapper.selectList(null);

                // 构建根节点
                List<FileDirectory> rootDirectories = allDirectories.stream().filter(d -> d.getParentId() == 0).collect(Collectors.toList());

                // 递归构建每个根节点的子文件夹列表
                rootDirectories.forEach(root -> {
                        buildChildren(root, allDirectories);
                });

                rootDirectories.forEach(r -> System.out.println("r = " + r));
        }

        private void buildChildren(FileDirectory parentDirectory, List<FileDirectory> allDirectories) {
                List<FileDirectory> children = allDirectories.stream().filter(d -> d.getParentId().equals(parentDirectory.getId())).collect(Collectors.toList());
                parentDirectory.setChildren(children);
                children.forEach(child -> {
                        buildChildren(child, allDirectories);
                });
        }

        @Test
        void redistest() {

                Object o = redisTemplate.opsForValue().get("login_tokens:df67b6db-1052-4708-a093-4da87a765d58");

                LoginUser user = objectMapper.convertValue(o, LoginUser.class);

                System.out.println("user = " + user);

        }


        @Test
        void test() {
                LocalDateTime now = LocalDateTime.now();

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/mm/dd");

                String format = now.format(dateTimeFormatter);

                System.out.println(format);

                LocalDateTime parse = LocalDateTime.parse(format, dateTimeFormatter);

                System.out.println("parse = " + parse);

        }

        @Test
        public void testUploadFiles() throws Exception {


                String bucketName = "photo-fruits";
                List<String> filesRecursively = getFilesRecursively("C:\\Users\\86159\\Desktop\\收银机图片");

                for (String filePath : filesRecursively) {
                        File file = new File(filePath);

                        // Generate MD5
                        String fileId = DigestUtils.md5DigestAsHex(UUID.fastUUID().toString().getBytes());

                        // Create bucket if not exists
                        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                        }

                        threadPoolTaskExecutor.submit(() -> {

                                try {
                                        // Upload to MinIO
                                        minioClient.uploadObject(
                                                UploadObjectArgs.builder()
                                                        .bucket(bucketName)
                                                        .object(fileId)
                                                        .filename(file.getAbsolutePath())
                                                        .build());

                                        // Get file URL


                                } catch (Exception e) {
                                        throw new RuntimeException(e);
                                }

                        });

                        threadPoolTaskExecutor.submit(() -> {

                                try {
                                        String url = minioClient.getObjectUrl(bucketName, fileId);
                                        // Save to database
                                        MediaFiles mediaFiles = new MediaFiles();
                                        mediaFiles.setId(fileId);
                                        mediaFiles.setFilePath(file.getAbsolutePath());
                                        mediaFiles.setFileId(fileId);
                                        mediaFiles.setUrl(url);
                                        mediaFiles.setFilename(file.getName());
                                        mediaFiles.setBucket(bucketName);
                                        mediaFiles.setFileType(FilenameUtils.getExtension(file.getName())); // You may need to adjust this line

                                        mediaFiles.setFileSize(file.length());

                                        mediaFilesService.save(mediaFiles);
                                } catch (Exception e) {
                                        throw new RuntimeException(e);}
                        });


                }
        }

        public List<String> getFilesRecursively(String directoryPath) {
                File directory = new File(directoryPath);
                String[] extensions = new String[]{"jpg", "png", "jpeg"}; // Only consider jpg, png, jpeg files.
                boolean recursive = true; // Do you want to include subdirectories?

                // Get files
                Collection<File> files = FileUtils.listFiles(directory, extensions, recursive);

                // Convert File objects to filenames and return
                return files.stream().map(File::getAbsolutePath).collect(Collectors.toList());
        }

}
