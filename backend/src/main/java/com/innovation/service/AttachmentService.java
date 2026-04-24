package com.innovation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.innovation.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService extends IService<Attachment> {

    Attachment uploadFile(MultipartFile file, String type, Integer relationId, Integer userId);

    Attachment getById(Integer id);
}
