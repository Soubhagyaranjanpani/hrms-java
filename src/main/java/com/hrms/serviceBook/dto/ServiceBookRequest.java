package com.hrms.serviceBook.dto;


import lombok.Data;

@Data
public class ServiceBookRequest {
    private String employeeName;
    private String employeeCode;
    private String department;
    private String designation;
    private String serviceBookNo;
}
