package com.hrms.attendance.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

@Entity
@Data
@Table(name = "attendance_policy")
public class AttendancePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalTime shiftStart;
    private LocalTime shiftEnd;

    private Integer graceMinutes;

    private Integer halfDayThresholdHours;
    private Integer fullDayHours;

    private Boolean allowOvertime;

    private Boolean isActive = true;

    // Extended shift and punch policy configuration
    private Integer checkInBeforeMinutes;

    private Boolean enforceCheckOutAfterShift;

    private Integer checkOutGraceMinutes;

    private Integer breakMinutes;

    private Boolean isNightShift;

    private String shiftCode;

    private String weeklyOffPolicy;
}

