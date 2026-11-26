package com.hrms.task.dto;

import lombok.Data;

import java.util.List;

@Data
public class DepartmentAssigneesResponse {
    private Long departmentId;
    private String departmentName;
    private List<AssigneeOptionResponse> employees;
}
