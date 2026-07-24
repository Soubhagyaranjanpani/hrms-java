package com.hrms.confirmation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ConfirmationRecordResponse {

    private Long id;
    private Long employeeId;
    private String employee;
    private String employeeCode;

    private String department;
    private String designation;

    private String confirmationOrderNumber;
    private LocalDate confirmationDate;

    private String confirmedBy;             // person's name
    private String confirmedByDesignation;   // role, e.g. "HR Manager"

    private Boolean isActive;

    private String documentPath;
    private String documentName;

    private String remarks;
    private String processedBy;
}
