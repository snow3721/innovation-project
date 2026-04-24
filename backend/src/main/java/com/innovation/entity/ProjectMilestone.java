package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("project_milestone")
public class ProjectMilestone {

    @TableId(type = IdType.AUTO)
    private Integer milestoneId;

    private Integer projectId;

    private String milestoneName;

    private LocalDate planTime;

    private LocalDate actualTime;

    private String status;

    private Integer isWarning;
}
