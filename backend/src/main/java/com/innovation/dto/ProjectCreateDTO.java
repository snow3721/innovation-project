package com.innovation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProjectCreateDTO {

    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    @NotNull(message = "项目类别不能为空")
    private Integer catId;

    @NotNull(message = "所属学院不能为空")
    private Integer collegeId;

    private BigDecimal totalBudget;

    private String startTime;

    private String endTime;

    private String content;

    private List<String> innovationPoints;

    private String techRoute;

    private Object budgetDetail;

    private List<MemberDTO> members;

    @Data
    public static class MemberDTO {
        private Integer userId;
        private String role;
    }
}
