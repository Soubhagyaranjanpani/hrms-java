package com.hrms.attendance.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(
        name = "attendance",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"employee_id", "date"})
        },
        indexes = {
                @Index(name = "idx_attendance_employee_date", columnList = "employee_id, date")
        }
)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Employee reference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // 📅 Attendance date
    @Column(nullable = false)
    private LocalDate date;

    // 📌 Status (ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status = AttendanceStatus.ABSENT;

    // ⏰ Time tracking
    private LocalTime checkIn;
    private LocalTime checkOut;

    // 📊 Calculated fields
    private Double workingHours;
    private Double overtimeHours;

    // 🧾 Flags
    private Boolean isManualEntry = false;

    private Boolean isDeleted = false;

    // 🧾 Audit fields
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    // 🔄 Lifecycle hooks
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
        if (this.status == null) {
            this.status = AttendanceStatus.ABSENT;
        }
    }

    // 🔥 ADD THESE
    @Column(nullable = false)
    private Boolean isLate = false;

    @Column(nullable = false)
    private Boolean isEarlyExit = false;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
