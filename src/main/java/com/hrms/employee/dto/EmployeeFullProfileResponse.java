package com.hrms.employee.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EmployeeFullProfileResponse {

    private Long id;
    private String employeeCode;
    private String name;
    private String email;
    private String phone;

    private String role;
    private String department;
    private String branch;

    private String managerName;

    private LocalDate joiningDate;

    // Enrichment
    private List<QualificationDto> qualifications;
    private List<SkillDto> skills;
    private List<TrainingDto> trainings;

    private List<DocumentDto> documents;

    // Leave summary
    private Double totalLeaveTaken;
    private Double remainingLeave;
}
