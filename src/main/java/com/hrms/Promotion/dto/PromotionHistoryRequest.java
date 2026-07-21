package com.hrms.promotion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PromotionHistoryRequest {

    @NotNull(message = "Employee is required")
    private Long employeeId;

    private Long oldBranchId;
    private Long newBranchId;

    private Long oldDepartmentId;
    private Long newDepartmentId;

    private Long oldDesignationId;

    @NotNull(message = "New Designation is required")
    private Long newDesignationId;

    @NotNull(message = "Promotion order number is required")
    private String promotionOrderNumber;

    @NotNull(message = "Promotion date is required")
    private LocalDate promotionDate;

    private Long promotionTypeId;

    private String oldGrade;
    private String newGrade;

    private LocalDate effectiveDate;

    private Long promotionAuthorityId;
}