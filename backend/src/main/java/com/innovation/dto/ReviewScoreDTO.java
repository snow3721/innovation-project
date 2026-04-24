package com.innovation.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ReviewScoreDTO {

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    @NotNull(message = "评审阶段不能为空")
    private String stage;

    private Integer scoreInnovation;

    private Integer scoreFeasibility;

    private Integer scoreTeam;

    private Integer scoreValue;

    @NotNull(message = "总分不能为空")
    private Integer totalScore;

    private String opinion;
}
