package com.hrms.employee.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeCreationReq {

    private String email;
    private String password;

    private String name;
    private String phone;
    private String address;
    private String profilePicture;

    private LocalDate joiningDate;

    private Long roleId;
    private Long departmentId;
    private Long branchId;
}
