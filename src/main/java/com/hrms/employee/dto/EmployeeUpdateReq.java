package com.hrms.employee.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeUpdateReq {

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String profilePicture;
    private LocalDate joiningDate;

    private Long roleId;
    private Long departmentId;
    private Long branchId;
    private Long managerId;

    private Boolean isActive;
}
