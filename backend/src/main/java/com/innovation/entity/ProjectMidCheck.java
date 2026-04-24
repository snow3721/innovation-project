package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("project_mid_check")
public class ProjectMidCheck {

    @TableId(type = IdType.AUTO)
    private Integer midId;

    private Integer projectId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime submitTime;

    private String status;
}
