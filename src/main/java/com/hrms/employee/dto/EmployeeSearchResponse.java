package com.hrms.employee.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSearchResponse {
    private Long id;
    private String employeeCode;
    private String name;
    private String email;
    private String department;
    private String role;
    private String branch;
}