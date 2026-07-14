package com.hrms.promotion.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PromotionRecordResponse {
    private Long id;
    private Long employeeId;
    private String employee;
    private String employeeCode;
    private String branch;           // employee's current branch (existing field, unchanged)

    private String promotionYear;
    private String promotionOrderNumber;
    private String promotionType;

    private String oldDesignation;
    private String newDesignation;
    private String oldDepartment;
    private String newDepartment;

    private String oldBranch;        // NEW
    private String newBranch;        // NEW

    private String previousGrade;
    private String newGrade;

    private Double oldSalary;
    private Double newSalary;
    private Double incrementAmount;
    private Double incrementPercent;

    private LocalDate promotionDate;
    private LocalDate effectiveDate;

    private String promotionAuthority;
    private String promotionAuthorityDesignation; // NEW — the role, e.g. "HR Director"

    private Boolean isActive;

    private String documentPath;
    private String documentName;

    private String remarks;
    private String aiInsight;
    private String processedBy;
}