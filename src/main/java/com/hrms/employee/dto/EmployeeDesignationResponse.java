package com.hrms.employee.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeDesignationResponse {
    private Long id;
    private String employeeName;
    private String designationName;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private Boolean isActive;
    private Boolean isDeleted;
}