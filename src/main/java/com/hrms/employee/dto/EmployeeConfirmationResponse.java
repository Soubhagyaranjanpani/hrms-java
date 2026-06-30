package com.hrms.employee.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EmployeeConfirmationResponse {

    private Long id;

    private Long employeeId;

    private String confirmationOrderNumber;

    private LocalDate confirmationDate;

    private Long confirmedById;

    private String confirmedByName;

    private String remarks;

    // Optional field (future use for document path)
    private String document;
}