package com.innovation.controller;

import com.innovation.common.Result;
import com.innovation.entity.Attachment;
import com.innovation.service.AttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "文件管理")
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private MinioClient minioClient;

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Result<Attachment> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam(value = "relationId", defaultValue = "0") Integer relationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        Attachment attachment = attachmentService.uploadFile(file, type, relationId, userId);
        return Result.success(attachment);
    }

    @ApiOperation("获取文件下载链接")
    @GetMapping("/{id}/download")
    public Result<String> getDownloadUrl(@PathVariable Integer id) {
        Attachment attachment = attachmentService.getById(id);
        if (attachment == null) {
            return Result.error(404, "文件不存在");
        }
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket("project-attachments")
                            .object(attachment.getMinioPath())
                            .method(Method.GET)
                            .expiry(60 * 30)
                            .build());
            return Result.success(url);
        } catch (Exception e) {
            return Result.error("获取下载链接失败");
        }
    }
}
