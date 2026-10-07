package com.hrms.serviceBook.dto;

import lombok.Data;

@Data
public class ServiceBookResponse {
    private Long id;
    private String employeeName;
    private String employeeCode;
    private String department;
    private String designation;
    private String serviceBookNo;
    private  String serviceBookName;
    private Boolean isActive;
    private Long empId;
    private String branchName;
}
