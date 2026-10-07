package com.hrms.leave.dto;

import com.hrms.leave.domain.enums.HalfDaySession;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveApplyRequest {

    private Long employeeId;
    private Long leaveTypeId;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isHalfDay;
    private HalfDaySession halfDaySession;

    private String reason;
}
