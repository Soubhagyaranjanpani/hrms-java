package com.hrms.promotion.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreatePromotionRequest {
    private Long employeeId;

    private String promotionOrderNumber;
    private Long promotionTypeId;

    private Long oldDesignationId;   // optional — falls back to employee's current active designation
    private Long newDesignationId;

    private Long oldDepartmentId;    // optional — falls back to employee's current department
    private Long newDepartmentId;

    private Long oldBranchId;        // optional — falls back to employee's current branch
    private Long newBranchId;        // optional — falls back to newDepartment's branch

    private Long previousGradeId;    // optional — falls back to employee's current grade
    private Long newGradeId;

    private Double oldSalary;        // optional — falls back to latest payroll record if available
    private Double newSalary;

    private LocalDate promotionDate;
    private LocalDate effectiveDate;

    private Long promotionAuthorityId;

    private String remarks;
}