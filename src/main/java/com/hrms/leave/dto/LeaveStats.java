package com.hrms.leave.dto;

import lombok.Data;

@Data
public class LeaveStats {
    private long pending;
    private long approved;
    private long rejected;
}
