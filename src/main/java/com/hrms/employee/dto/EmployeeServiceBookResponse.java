package com.hrms.employee.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeServiceBookResponse {

    private Long employeeId;
    private String employeeCode;
    private String name;
    private String role;
    private String department;
    private String branch;

    // 🔥 Service Lifecycle
    private List<ServiceHistoryDto> serviceHistory;

    // 📄 Documents
    private List<DocumentDto> documents;

    // 🌴 Leaves
    private List<LeaveDto> leaves;

    // 🎓 Profile enrichment
    private List<QualificationDto> qualifications;
    private List<SkillDto> skills;
    private List<TrainingDto> trainings;
}
