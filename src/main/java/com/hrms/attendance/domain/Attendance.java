package com.hrms.attendance.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "attendance",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"employee_id", "date"})
        }
)
@Data
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Employee reference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Attendance date
    @Column(nullable = false)
    private LocalDate date;

    // Status using ENUM
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    // Time tracking
    private LocalTime checkIn;
    private LocalTime checkOut;

    // Calculated fields
    private Double workingHours;
    private Double overtimeHours;

    // Audit fields
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    // Soft delete flag
    private Boolean isDeleted = false;

    // Lifecycle hooks
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
