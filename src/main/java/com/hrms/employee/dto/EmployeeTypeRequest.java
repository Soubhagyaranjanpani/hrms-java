package com.hrms.employee.dto;

import lombok.Data;

@Data
public class EmployeeTypeRequest{
    private String employmentType;
    private String createdBy;

    private String updatedBy;
}