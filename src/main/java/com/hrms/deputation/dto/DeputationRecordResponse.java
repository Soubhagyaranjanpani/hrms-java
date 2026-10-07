package com.hrms.deputation.dto;

import com.hrms.master.dto.DeputationTypeResponse;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class DeputationRecordResponse {
    private Long id;
    private Long employeeId;
    private String employee;
    private String employeeCode;
    private String department;
    private String designation;
    private String deputationOrderNumber;
    private String deputationOrganization;
    private LocalDate startDate;
    private LocalDate endDate;
    private DeputationTypeResponse deputationType;
    private String reportingAuthority;
    private String reportingAuthorityDesignation;
    private Boolean isActive;
    private String documentPath;
    private String documentName;
    private String remarks;
    private String processedBy;
}