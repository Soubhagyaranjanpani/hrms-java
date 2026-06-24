package com.hrms.employee.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmployeeTypeResponse {

    private Long id;

    private String employmentType;

    private Boolean isActive;

    private Boolean isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}