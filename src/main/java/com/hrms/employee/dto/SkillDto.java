package com.hrms.employee.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SkillDto {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long skillId;
    private String skillName;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private Boolean isActive;
    private Boolean isDeleted;
}