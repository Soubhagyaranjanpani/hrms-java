package com.hrms.task.dto;

import lombok.Data;

@Data
public class ReviewSubmissionRequest {
    private Boolean approved;
    private String comment;   // required when rejecting
}
