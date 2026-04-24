package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("project")
public class Project {

    @TableId(type = IdType.AUTO)
    private Integer projectId;

    private String projectName;

    private Integer catId;

    private Integer leaderId;

    private Integer teacherId;

    private Integer collegeId;

    private Integer applyYear;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime applyTime;

    private BigDecimal totalBudget;

    private String status;

    private LocalDate startTime;

    private LocalDate endTime;

    @TableField(exist = false)
    private String leaderName;

    @TableField(exist = false)
    private String teacherName;

    @TableField(exist = false)
    private String collegeName;

    @TableField(exist = false)
    private String catName;

    @TableField(exist = false)
    private String statusText;
}
