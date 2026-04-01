package com.hrms.employee.dto;

import lombok.Data;

@Data
public class SkillRequest {

    private Long employeeId;
    private String skillName;
    private Integer proficiency;
}
