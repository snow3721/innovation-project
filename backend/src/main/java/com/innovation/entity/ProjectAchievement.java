package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("project_achievement")
public class ProjectAchievement {

    @TableId(type = IdType.AUTO)
    private Integer achievementId;

    private Integer projectId;

    private String type;

    private String name;

    private String achievementNo;

    private LocalDate publishTime;

    private String status;

    @TableField(exist = false)
    private String projectName;
}
