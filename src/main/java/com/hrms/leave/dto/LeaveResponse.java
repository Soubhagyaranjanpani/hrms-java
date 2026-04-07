package com.hrms.leave.dto;

import com.hrms.leave.domain.enums.LeaveStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor   // 🔥 ensure default constructor exists
public class LeaveResponse {

    private Long leaveId;
    private String employeeName;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalDays;
    private LeaveStatus status;

    public LeaveResponse(Long leaveId,
                         String employeeName,
                         String leaveType,
                         LocalDate startDate,
                         LocalDate endDate,
                         Double totalDays,
                         LeaveStatus status) {

        this.leaveId = leaveId;
        this.employeeName = employeeName;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.status = status;
    }
}
