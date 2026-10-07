package com.hrms.master.dto;

import lombok.Data;

@Data
public class DepartmentUpdateReq {
    private Long id;
    private String name;
    private Long branchId;
}
