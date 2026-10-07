package com.hrms.disciplinary.dto;

import com.hrms.master.dto.ActionTypeResponse;
import com.hrms.master.dto.PenaltyTypeResponse;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class DisciplinaryRecordResponse {
    private Long id;
    private Long employeeId;
    private String employee;
    private String employeeCode;
    private String department;
    private String designation;
    private String caseNumber;
    private LocalDate incidentDate;

    // ✅ Dropdown 1
    private ActionTypeResponse actionType;

    // ✅ Dropdown 2 - Investigation Officer
    private Long investigationOfficerId;
    private String investigationOfficerName;
    private String investigationOfficerDesignation;

    // ✅ Dropdown 3
    private PenaltyTypeResponse penaltyType;

    private LocalDate resolutionDate;
    private String remarks;
    private Boolean isActive;
    private String documentPath;
    private String documentName;
    private String processedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}