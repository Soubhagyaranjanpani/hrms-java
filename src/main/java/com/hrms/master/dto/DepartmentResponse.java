package com.hrms.master.dto;

import lombok.Data;

@Data
public class DepartmentResponse {
    private Long id;
    private String code;
    private String name;
    private Long branchId;
    private String branchName;
    private Boolean isActive;
}