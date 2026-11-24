package com.hrms.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftResponse {

    private Long id;
    private String code;
    private String shortCode;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double durationHours;
    private Integer breakMinutes;
    private Integer graceMinutes;
    private Boolean isNightShift;
    private String color;
    private String bgColor;
    private String borderColor;
    private Boolean isActive;
    private String description;
}
