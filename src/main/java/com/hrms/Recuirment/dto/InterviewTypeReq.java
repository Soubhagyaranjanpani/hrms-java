package com.hrms.Recuirment.dto;

import lombok.Data;

@Data
public class InterviewTypeReq {
    private String interviewTypeCode;
    private String interviewTypeName;
    private String description;
    private String status;
    private Long interviewModeId;
}
