package com.hrms.employee.dto;

import com.hrms.leave.domain.enums.LeaveStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveDto {

    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double days;
    private LeaveStatus status;
}
