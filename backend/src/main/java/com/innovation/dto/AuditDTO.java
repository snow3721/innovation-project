package com.innovation.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AuditDTO {

    @NotNull(message = "项目ID不能为空")
    private Integer projectId;

    @NotNull(message = "审核结果不能为空")
    private String result;

    private String opinion;
}
