package com.hrms.attendance.dto;

import lombok.Data;

@Data
public class AttendanceSummaryResponse {

    private int presentDays;
    private int absentDays;
    private int leaveDays;
    private int halfDays;

    private double totalWorkingHours;
}
