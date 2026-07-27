package com.hrms.deputation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateDeputationRequest {

    private Long employeeId;

    private String deputationOrderNumber;
    private String deputationOrganization;

    private LocalDate startDate;
    private LocalDate endDate;

    private String deputationType; // Domestic Deputation / Government / Project Based / Training / International

    private Long reportingAuthorityId; // EmployeeDesignation id

    // departmentName/designationName are NOT part of the request — they're
    // auto-populated server-side from the employee's current record.
}
