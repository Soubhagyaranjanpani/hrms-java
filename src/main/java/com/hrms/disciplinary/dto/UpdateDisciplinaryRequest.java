package com.hrms.disciplinary.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateDisciplinaryRequest {
    private String caseNumber;
    private LocalDate incidentDate;
    private Long actionTypeId;
    private Long investigationOfficerId;
    private Long penaltyTypeId;
    private LocalDate resolutionDate;
    private String remarks;
}