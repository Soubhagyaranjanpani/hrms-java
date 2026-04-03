package com.hrms.master.dto;

import lombok.Data;

@Data
public class DepartmentCreateReq {
    private String code;
    private String name;
    private Long branchId;
}