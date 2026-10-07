package com.hrms.payrevision.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreatePayRevisionRequest {

    private Long employeeId;

    private String payRevisionOrderNumber;
    private LocalDate effectiveDate;

    private Double previousPayScaleMin; // optional — falls back to employee's last revision's revised-max, or current salary
    private Double previousPayScaleMax;

    private Double revisedPayScaleMin;
    private Double revisedPayScaleMax;

    private Long reasonId; // PayRevisionReason id

    private String remarks;
}
