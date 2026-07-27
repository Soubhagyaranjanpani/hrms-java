package com.hrms.deputation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DeputationRecordResponse {

    private Long id;
    private Long employeeId;
    private String employee;
    private String employeeCode;

    private String department; // snapshot
    private String designation; // snapshot

    private String deputationOrderNumber;
    private String deputationOrganization;

    private LocalDate startDate;
    private LocalDate endDate;

    private String deputationType;

    private String reportingAuthority;             // person's name
    private String reportingAuthorityDesignation;   // role, e.g. "HR Director"

    private Boolean isActive;

    private String documentPath;
    private String documentName;

    private String processedBy;
}
