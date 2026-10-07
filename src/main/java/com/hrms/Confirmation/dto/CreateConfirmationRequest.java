package com.hrms.Confirmation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateConfirmationRequest {

    private Long employeeId;

    private String confirmationOrderNumber;
    private LocalDate confirmationDate;

    private Long confirmedById; // EmployeeDesignation id (the "Confirmed By" authority)

    private String remarks;

    // departmentName/designationName are NOT part of the request — they're
    // auto-populated server-side from the employee's current record (see
    // CreateConfirmationUseCase), matching the form's read-only behavior.
}
