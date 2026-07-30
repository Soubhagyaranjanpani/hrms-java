package com.hrms.disciplinary.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class CreateDisciplinaryRequest {
    private Long employeeId;
    private String caseNumber;
    private LocalDate incidentDate;
    private Long actionTypeId;           // ✅ Dropdown 1
    private Long investigationOfficerId; // ✅ Dropdown 2
    private Long penaltyTypeId;          // ✅ Dropdown 3
    private LocalDate resolutionDate;
    private String remarks;
}