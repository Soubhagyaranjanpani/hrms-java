package com.hrms.promotion.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PromotionHistoryResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;

    private Long oldBranchId;
    private Long newBranchId;

    private Long oldDepartmentId;
    private Long newDepartmentId;

    private Long oldDesignationId;
    private Long newDesignationId;

    private String promotionOrderNumber;
    private LocalDate promotionDate;
    private Long promotionTypeId;

    private String oldGrade;
    private String newGrade;
    private LocalDate effectiveDate;

    private Long promotionAuthorityId;

    private String status;
}