package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attachment")
public class Attachment {

    @TableId(type = IdType.AUTO)
    private Integer attachId;

    private String attachType;

    private Integer relationId;

    private String fileName;

    private Long fileSize;

    private String minioPath;

    private Integer uploadUser;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime uploadTime;
}
