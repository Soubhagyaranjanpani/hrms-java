package com.hrms.employee.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SkillRequest {

    @NotNull(message = "employeeId is required")
    private Long employeeId;

    @NotNull(message = "skillId is required")
    private Long skillId;
}