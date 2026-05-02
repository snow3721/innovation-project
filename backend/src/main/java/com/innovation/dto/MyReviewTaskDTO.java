package com.innovation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyReviewTaskDTO {

    /** 任务类型：review=评审打分，audit=审核 */
    private String type;

    private Integer assignmentId;

    private Integer projectId;

    private String projectName;

    /** 评审阶段（review类型）：college/school */
    private String stage;

    /** 审核类型（audit类型）：teacher_audit/college_audit/school_audit */
    private String auditType;

    private Boolean scored;

    private Integer scoreId;

    private LocalDateTime deadline;

    private LocalDateTime assignTime;

    private String projectStatus;
}
