package com.hrms.Recuirment.dto;

import lombok.Data;

@Data
public class CandidateStatusReq {

    private String statusCode;
    private String statusName;
    private Integer displayOrder;
    private String finalStatus;
    private String statusColor;
    private String description;
    private String status;
    private Long statusCategoryId;
}