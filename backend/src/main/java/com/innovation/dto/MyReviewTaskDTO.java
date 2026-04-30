package com.innovation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyReviewTaskDTO {

    private Integer assignmentId;

    private Integer projectId;

    private String projectName;

    private String stage;

    private Boolean scored;

    private Integer scoreId;

    private LocalDateTime deadline;

    private LocalDateTime assignTime;
}
