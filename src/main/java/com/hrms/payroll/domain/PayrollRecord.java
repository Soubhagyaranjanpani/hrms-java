package com.hrms.payroll.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "payroll_records",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id","year_month"}))
public class PayrollRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "year_month", nullable = false, length = 7)
    private String yearMonth;         // "2025-03"

    @Column(nullable = false, length = 20)
    private String payrollMonth;      // "March 2025"

    // ── Earnings ──────────────────────────────────
    private Double basicSalary    = 0.0;
    private Double hra            = 0.0;   // House Rent Allowance
    private Double travelAllow    = 0.0;   // Travel Allowance
    private Double medicalAllow   = 0.0;   // Medical Allowance
    private Double specialAllow   = 0.0;   // Special Allowance
    private Double otherEarnings  = 0.0;

    // ── Deductions ────────────────────────────────
    private Double providentFund  = 0.0;   // PF (12% of basic)
    private Double professionalTax= 0.0;   // PT
    private Double incomeTax      = 0.0;   // TDS
    private Double loanDeduction  = 0.0;
    private Double otherDeductions= 0.0;

    // ── Computed ──────────────────────────────────
    private Double grossEarnings  = 0.0;   // sum of all earnings
    private Double totalDeductions= 0.0;   // sum of all deductions
    private Double netSalary      = 0.0;   // gross - totalDeductions

    // ── Working days ──────────────────────────────
    private Integer workingDays   = 0;
    private Integer paidDays      = 0;
    private Integer lopDays       = 0;     // Loss Of Pay

    // ── Status ────────────────────────────────────
    // DRAFT → PENDING → APPROVED → PROCESSED → PAID
    @Column(nullable = false, length = 20)
    private String status = "DRAFT";

    private LocalDate paymentDate;

    private String remarks;

    // ── AI-generated insight ──────────────────────
    @Column(length = 500)
    private String aiInsight;              // e.g. "Salary increased 8% vs last month"

    // ── Audit ─────────────────────────────────────
    private Boolean isDeleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String processedBy;

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

    public void compute() {
        this.grossEarnings = safe(basicSalary) + safe(hra) + safe(travelAllow)
                + safe(medicalAllow) + safe(specialAllow) + safe(otherEarnings);
        this.totalDeductions = safe(providentFund) + safe(professionalTax)
                + safe(incomeTax) + safe(loanDeduction) + safe(otherDeductions);
        this.netSalary = this.grossEarnings - this.totalDeductions;
        // LOP adjustment
        if (workingDays != null && workingDays > 0 && lopDays != null && lopDays > 0) {
            double perDay = this.grossEarnings / workingDays;
            this.netSalary = this.netSalary - (perDay * lopDays);
        }
    }

    private double safe(Double v) { return v != null ? v : 0.0; }
}