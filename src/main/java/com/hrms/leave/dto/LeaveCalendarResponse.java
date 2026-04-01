package com.hrms.leave.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LeaveCalendarResponse {

    private LocalDate date;
    private List<String> employeesOnLeave;
}
