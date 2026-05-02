package com.innovation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("project_review_score")
public class ProjectReviewScore {

    @TableId(type = IdType.AUTO)
    private Integer scoreId;

    private Integer projectId;

    private Integer expertId;

    private String reviewStage;

    private Integer scoreInnovation;

    private Integer scoreFeasibility;

    private Integer scoreTeam;

    private Integer scoreValue;

    private Integer totalScore;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime scoreTime;

    @TableField(exist = false)
    private String expertName;

    @TableField(exist = false)
    private String projectName;
}
