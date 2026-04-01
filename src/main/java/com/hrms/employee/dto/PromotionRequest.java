package com.hrms.employee.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PromotionRequest {

    private Long employeeId;
    private Long designationId; // instead of String
    private LocalDate effectiveDate;
    private String orderReference;
}
