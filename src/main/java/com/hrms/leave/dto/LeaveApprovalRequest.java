package com.hrms.leave.dto;

import lombok.Data;

@Data
public class LeaveApprovalRequest {

    private Long leaveId;
    private String remarks;
}
