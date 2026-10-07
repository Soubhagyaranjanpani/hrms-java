package com.hrms.Recuirment.dto;

import lombok.Data;

@Data
public class NoticePeriodCreateReq {

    private String periodCode;
    private String periodName;
    private Integer days;
    private String description;
    private String status;
}
