package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("expert_assignment")
public class ExpertAssignment {

    @TableId(type = IdType.AUTO)
    private Integer assignmentId;

    private Integer projectId;

    private Integer expertId;

    private String stage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime assignTime;

    private LocalDateTime deadline;

    @TableField(exist = false)
    private String expertName;
}
