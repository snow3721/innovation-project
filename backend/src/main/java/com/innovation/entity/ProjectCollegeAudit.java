package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("project_college_audit")
public class ProjectCollegeAudit {

    @TableId(type = IdType.AUTO)
    private Integer auditId;

    private Integer projectId;

    private Integer adminId;

    private String result;

    private String opinion;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime auditTime;
}
