package com.hrms.promotion.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class UpdatePromotionRequest {
    private String promotionOrderNumber;
    private String promotionType;

    private String oldDesignation;
    private String newDesignation;
    private String oldDepartment;
    private String newDepartment;

    private String previousGrade;
    private String newGrade;

    private Double oldSalary;
    private Double newSalary;

    private LocalDate promotionDate;
    private LocalDate effectiveDate;

    private String promotionAuthority;
    private String remarks;
}