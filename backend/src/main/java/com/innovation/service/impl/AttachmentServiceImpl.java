package com.innovation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.config.MinioConfig;
import com.innovation.entity.Attachment;
import com.innovation.mapper.AttachmentMapper;
import com.innovation.service.AttachmentService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements AttachmentService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".pdf", ".doc", ".docx");

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public Attachment uploadFile(MultipartFile file, String type, Integer relationId, Integer userId) {
        try {
            // 文件格式校验
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase()
                    : "";
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new RuntimeException("不支持的文件格式，仅支持 PDF/Word 文档（.pdf/.doc/.docx）");
            }

            // 文件大小校验
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new RuntimeException("文件大小超过限制，最大允许 10MB");
            }

            String fileName = UUID.randomUUID().toString() + extension;
            String minioPath = type + "/" + relationId + "/" + fileName;

            String bucketName = minioConfig.getDefaultBucket();
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            InputStream inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(minioPath)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            inputStream.close();

            Attachment attachment = new Attachment();
            attachment.setAttachType(type);
            attachment.setRelationId(relationId);
            attachment.setFileName(originalFilename);
            attachment.setFileSize(file.getSize());
            attachment.setMinioPath(minioPath);
            attachment.setUploadUser(userId);
            save(attachment);
            return attachment;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public Attachment getById(Integer id) {
        return super.getById(id);
    }
}
