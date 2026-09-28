package com.hrms.Recuirment.dto;

import lombok.Data;

@Data
public class InterviewRoundReq {
    private String roundCode;
    private String roundName;
    private String roundSequence;
    private String mandatory;
    private String maximumScore;
    private String passingScore;
    private String status;
    private Long departmentId;
}
