package com.hrms.leave.dto;

import com.hrms.leave.domain.enums.LeaveStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveResponse {

    private Long leaveId;
    private String employeeName;
    private String leaveType;

    private LocalDate startDate;
    private LocalDate endDate;

    private Double totalDays;
    private LeaveStatus status;
}
