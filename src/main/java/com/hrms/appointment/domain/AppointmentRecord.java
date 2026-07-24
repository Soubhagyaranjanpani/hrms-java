package com.hrms.appointment.domain;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.Designation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "appointment_records")
public class AppointmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "appointment_order_number", unique = true, length = 100)
    private String appointmentOrderNumber;

    @Column(name = "appointment_date")
    private LocalDate appointmentDate;

    // ── Authority (person who signs the appointment letter) ──
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_authority", nullable = false)
    private EmployeeDesignation appointmentAuthority;

    // ── Type dropdowns ──
    // Stored as plain strings since (unlike PromotionType) there is no dedicated
    // master table shown in the UI for these. Swap to an enum or a master-table
    // FK the same way PromotionType/EmployeeGrade are used if you add one later.
    @Column(name = "appointment_type", length = 30)
    private String appointmentType; // Permanent / Contract / Temporary

    @Column(name = "employment_type", length = 30)
    private String employmentType; // Full-Time / Part-Time / Contractual

    // ── Initial posting ──
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initial_designation", nullable = false)
    private Designation initialDesignation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initial_department", nullable = false)
    private Department initialDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initial_branch", nullable = false)
    private Branch initialBranch;

    // ── Dates / probation ──
    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "probation_period_months")
    private Integer probationPeriodMonths = 6;

    @Column(name = "confirmation_due_date")
    private LocalDate confirmationDueDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "document_path", length = 500)
    private String documentPath;

    @Column(name = "document_name", length = 255)
    private String documentName;

    private String remarks;

    private Boolean isDeleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String processedBy;

    /** Recomputes confirmationDueDate from joiningDate + probationPeriodMonths. */
    public void compute() {
        if (joiningDate != null && probationPeriodMonths != null) {
            this.confirmationDueDate = joiningDate.plusMonths(probationPeriodMonths);
        }
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        compute();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        compute();
    }
}
