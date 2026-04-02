package com.hrms.attendance.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceLeaveDashboardResponse {

    private LocalDate date;

    private String status;
    // PRESENT, ABSENT, LEAVE, HOLIDAY, WEEKEND

    private String leaveType; // optional

    private Double workingHours;
}
