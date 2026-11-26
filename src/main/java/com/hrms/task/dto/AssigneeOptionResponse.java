package com.hrms.task.dto;

import lombok.Data;

@Data
public class AssigneeOptionResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Long departmentId;
    private String department;
}
