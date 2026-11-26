package com.hrms.leave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceResponse {
    private Long leaveTypeId;
    private String leaveTypeName;
    private Double totalAllocated;
    private Double remaining;
    private Double used;
    private Integer year;
    private Integer maxDaysPerYear;
}
