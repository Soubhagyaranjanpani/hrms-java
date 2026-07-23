package com.hrms.employment_type.dto;

import lombok.Data;

@Data
public class EmploymentTypeResponse {
    private Long id;
    private String name;
    private Boolean isActive;
}