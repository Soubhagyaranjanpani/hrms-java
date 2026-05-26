package com.hrms.task.dto;


import lombok.Data;

@Data
public class EmployeeTaskStatsResponse {

    private Long employeeId;
    private String employeeName;

    private Long totalAssigned;
    private Long pendingApproval;
    private Long inProgress;
    private Long completed;
    private Long overdue;
}
