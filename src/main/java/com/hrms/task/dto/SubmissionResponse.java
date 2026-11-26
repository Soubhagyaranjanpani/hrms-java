package com.hrms.task.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SubmissionResponse {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private Long employeeId;
    private String employeeName;
    private String note;
    private String proofLink;
    private String proofFileName;
    private Boolean hasProofFile;
    private Integer progressPercent;
    private Double hoursSpent;
    private Boolean finalSubmission;
    private Boolean late;
    private LocalDate submissionDate;
    private LocalDateTime submittedAt;
    private String status;
    private String reviewedBy;
    private String reviewComment;
    private LocalDateTime reviewedAt;
}
