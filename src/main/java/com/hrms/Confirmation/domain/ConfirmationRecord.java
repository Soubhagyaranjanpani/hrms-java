package com.hrms.confirmation.domain;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "confirmation_records")
public class ConfirmationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "confirmation_order_number", unique = true, length = 100)
    private String confirmationOrderNumber;

    @Column(name = "confirmation_date")
    private LocalDate confirmationDate;

    // ── Authority who confirms the employee ("Confirmed By") ──
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by", nullable = false)
    private EmployeeDesignation confirmedBy;

    // ── Snapshot of department/designation at the time of confirmation ──
    // Unlike Promotion/Appointment, the form only shows these as read-only
    // "Auto-populated" fields (no dropdown to pick a different value), so they're
    // captured here as a point-in-time snapshot rather than as FK relations.
    @Column(name = "department_name", length = 150)
    private String departmentName;

    @Column(name = "designation_name", length = 150)
    private String designationName;

    private String remarks;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "document_path", length = 500)
    private String documentPath;

    @Column(name = "document_name", length = 255)
    private String documentName;

    private Boolean isDeleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String processedBy;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
