package com.hrms.employee.dto;

import lombok.Data;

@Data
public class SkillDto {

    private Long id;
    private Long employeeId;
    private String skillName;
    private Integer proficiency;
}