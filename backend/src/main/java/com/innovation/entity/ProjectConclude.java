package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("project_conclude")
public class ProjectConclude {

    @TableId(type = IdType.AUTO)
    private Integer concludeId;

    private Integer projectId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime submitTime;

    private String status;
}
