package com.hrms.retirement.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RetirementRecordResponse {

    private Long id;
    private Long employeeId;
    private String employee;
    private String employeeCode;

    private String department; // snapshot
    private String designation; // snapshot

    private LocalDate retirementDate;

    private Long retirementTypeId;
    private String retirementType; // name, for display

    private Long pensionEligibilityId;
    private String pensionEligibility; // name, for display

    private String pensionNumber;
    private String retirementOrder;
    private String retirementBenefits;

    private Boolean isActive;

    private String documentPath;
    private String documentName;

    private String processedBy;
}
