package com.hrms.attendance.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceResponse {

    private Long id;
    private String employeeName;

    private LocalDate date;

    private LocalTime checkIn;
    private LocalTime checkOut;

    private Double workingHours;
    private Double overtimeHours;

    private String status;
}
