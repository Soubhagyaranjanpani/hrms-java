package com.hrms.payrevision.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PayRevisionRecordResponse {

    private Long id;
    private Long employeeId;
    private String employee;
    private String employeeCode;

    private String payRevisionOrderNumber;
    private LocalDate effectiveDate;

    private Double previousPayScaleMin;
    private Double previousPayScaleMax;
    private Double revisedPayScaleMin;
    private Double revisedPayScaleMax;

    private Double incrementAmount;
    private Double incrementPercent;

    private Long reasonId;
    private String reason;

    private Boolean isActive;

    private String documentPath;
    private String documentName;

    private String remarks;
    private String processedBy;
}
