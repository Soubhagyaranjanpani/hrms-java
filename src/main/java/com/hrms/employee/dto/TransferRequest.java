package com.hrms.employee.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransferRequest {

    private Long employeeId;
    private Long newDepartmentId;
    private Long newBranchId;

    private LocalDate effectiveDate;
    private String orderReference;
}
