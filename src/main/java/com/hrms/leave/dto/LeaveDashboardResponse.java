package com.hrms.leave.dto;

import lombok.Data;

import java.util.List;

@Data
public class LeaveDashboardResponse {

    private List<LeaveResponse> myLeaves;
    private List<LeaveResponse> teamLeaves;
    private List<LeaveResponse> pendingApprovals;
    private List<LeaveResponse> allLeaves;
    private LeaveStats stats;
}
