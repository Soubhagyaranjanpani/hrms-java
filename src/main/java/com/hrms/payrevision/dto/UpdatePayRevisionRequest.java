package com.hrms.payrevision.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdatePayRevisionRequest {

    private String payRevisionOrderNumber;
    private LocalDate effectiveDate;
    private Double revisedPayScaleMin;
    private Double revisedPayScaleMax;
    private String remarks;
}
