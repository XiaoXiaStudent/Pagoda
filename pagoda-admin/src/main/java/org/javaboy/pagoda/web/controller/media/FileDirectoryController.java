package org.javaboy.pagoda.web.controller.media;


import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.jni.Directory;
import org.javaboy.pagoda.common.annotation.Log;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.core.domain.entity.SysUser;
import org.javaboy.pagoda.common.enums.BusinessType;
import org.javaboy.pagoda.common.utils.poi.ExcelUtil;
import org.javaboy.pagoda.media.entity.FileDirectory;
import org.javaboy.pagoda.media.entity.MediaFiles;
import org.javaboy.pagoda.media.service.IFileDirectoryService;
import org.javaboy.pagoda.media.service.IMediaFilesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 文件夹表 前端控制器
 * </p>
 *
 * @author javaboy
 * @since 2023-05-18
 */
@RestController
@RequestMapping("/media/file-directory")
public class FileDirectoryController {

        @Resource
        IFileDirectoryService fileDirectoryService;

        @Resource
        IMediaFilesService mediaFilesService;

        @GetMapping("/listdirecotry")
        public AjaxResult listDirectory() {
                return fileDirectoryService.listDirectory();
        }

        @GetMapping("/files/{directoryId}")
        public AjaxResult listFilesInDirectory(@PathVariable Long directoryId) {
                return mediaFilesService.getFilesInDirectory(directoryId);

        }

        @PostMapping("/upload")
        public AjaxResult upload(MultipartFile file, Long directoryId) throws IOException {


                MediaFiles mediaFiles = new MediaFiles();

                mediaFiles.setDirectoryId(directoryId);
                mediaFiles.setCreateDate(LocalDateTime.now());
                mediaFiles.setFileSize(file.getSize());
                mediaFiles.setFileType(file.getContentType());
                mediaFiles.setFilename(file.getOriginalFilename());


                return mediaFilesService.upload(file.getBytes(), mediaFiles);
        }

        @PostMapping("/add")
        public AjaxResult createDirectory(@RequestBody FileDirectory fileDirectory) {

                return fileDirectoryService.add(fileDirectory);
        }

        @ApiOperation(value = "分块文件上传前的检测")
        @PostMapping("/upload/checkchunk")
        public AjaxResult checkchunk(@RequestParam("fileMd5") String fileMd5,
                                                @RequestParam("chunk") int chunk) throws Exception {
                return mediaFilesService.checkChunk(fileMd5, chunk);
        }

        @ApiOperation(value = "上传分块文件")
        @PostMapping("/upload/uploadchunk")
        public AjaxResult uploadchunk(@RequestParam("file") MultipartFile file,
                                      @RequestParam("fileMd5") String fileMd5,
                                      @RequestParam("chunk") int chunk) throws Exception {

                return mediaFilesService.uploadchunk(file.getBytes(), chunk, fileMd5 , file.getContentType());

        }

        @ApiOperation(value = "合并文件")
        @PostMapping("/upload/mergechunks")
        public AjaxResult mergechunks(@RequestParam("fileMd5") String fileMd5,
                                        @RequestParam("fileName") String fileName,
                                        @RequestParam("chunkTotal") int chunkTotal, Long directoryId) throws Exception {

                return mediaFilesService.mergechunks(fileMd5, fileName, chunkTotal, directoryId);
        }




}
