package com.hrms.deputation.domain;

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
@Table(name = "deputation_records")
public class DeputationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "deputation_order_number", unique = true, length = 100)
    private String deputationOrderNumber;

    // Free-text external organization name (e.g. "Ministry of Corporate Affairs",
    // "PwC India") — no master table for this, unlike department/branch elsewhere.
    @Column(name = "deputation_organization", length = 255)
    private String deputationOrganization;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Stored as plain string since the form uses a simple dropdown
    // (Domestic Deputation / Government / Project Based / Training / International).
    @Column(name = "deputation_type", length = 40)
    private String deputationType;

    // ── Reporting Authority at the deputation location ──
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_authority", nullable = false)
    private EmployeeDesignation reportingAuthority;

    // Snapshot of department/designation at the time of deputation — the form shows
    // these as read-only "Auto-populated" fields; a deputation doesn't change them.
    @Column(name = "department_name", length = 150)
    private String departmentName;

    @Column(name = "designation_name", length = 150)
    private String designationName;

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
