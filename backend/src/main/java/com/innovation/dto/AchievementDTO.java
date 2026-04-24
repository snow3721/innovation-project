package com.innovation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AchievementDTO {

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    @NotBlank(message = "成果类型不能为空")
    private String type;

    @NotBlank(message = "成果名称不能为空")
    private String name;

    private String achievementNo;

    private String publishTime;

    @NotBlank(message = "成果状态不能为空")
    private String status;

    private Object extend;

    private java.util.List<Integer> attachmentIds;
}
