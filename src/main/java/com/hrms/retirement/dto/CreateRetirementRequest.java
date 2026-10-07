package com.hrms.retirement.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateRetirementRequest {

    private Long employeeId;

    private LocalDate retirementDate;

    private Long retirementTypeId;       // → RetirementType master table id
    private Long pensionEligibilityId;   // → PensionEligibility master table id

    private String pensionNumber;
    private String retirementOrder;
    private String retirementBenefits;

    // departmentName/designationName are NOT part of the request — they're
    // auto-populated server-side from the employee's current record.
}
