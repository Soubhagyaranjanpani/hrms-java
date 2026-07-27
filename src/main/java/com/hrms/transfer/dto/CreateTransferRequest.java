package com.hrms.transfer.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTransferRequest {

    private Long employeeId;

    private String transferOrderNumber;
    private LocalDate transferDate;
    private String transferType; // Permanent Transfer / Temporary / On Deputation

    private Long toDepartmentId;  // required — the new department
    private Long toBranchId;      // required — the new branch

    private LocalDate effectiveDate;
    private String transferReason;

    // fromDepartment/fromBranch/designation are NOT part of the request — they're
    // auto-populated server-side from the employee's current record.
}
