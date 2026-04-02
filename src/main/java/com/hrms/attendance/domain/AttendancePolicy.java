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
}
