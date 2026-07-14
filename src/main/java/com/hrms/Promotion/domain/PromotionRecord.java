package com.hrms.promotion.domain;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.employee.domain.EmployeeGrade;
import com.hrms.employee.domain.PromotionType;
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
@Table(name = "promotion_records")
public class PromotionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "promotion_year", length = 4)
    private String promotionYear;

    @Column(name = "promotion_order_number", unique = true, length = 100)
    private String promotionOrderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_type", nullable = false)
    private PromotionType promotionType;

    // ── Designation change ─────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_designation", nullable = false)
    private Designation oldDesignation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_designation", nullable = false)
    private Designation newDesignation;

    // ── Department change ─────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_department", nullable = false)
    private Department oldDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_department", nullable = false)
    private Department newDepartment;

    // ── Branch change (NEW) ─────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_branch", nullable = false)
    private Branch oldBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_branch", nullable = false)
    private Branch newBranch;

    // ── Grade change ──────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_grade", nullable = false)
    private EmployeeGrade previousGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_grade", nullable = false)
    private EmployeeGrade newGrade;

    // ── Salary ──
    private Double oldSalary = 0.0;
    private Double newSalary = 0.0;

    private Double incrementAmount  = 0.0;
    private Double incrementPercent = 0.0;

    // ── Dates ────────────────────────────────────────
    private LocalDate promotionDate;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    // ── Authority ──────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_authority", nullable = false)
    private EmployeeDesignation promotionAuthority;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "document_path", length = 500)
    private String documentPath;

    @Column(name = "document_name", length = 255)
    private String documentName;

    private String remarks;

    @Column(length = 500)
    private String aiInsight;

    private Boolean isDeleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String processedBy;

    public void compute() {
        double old = safe(oldSalary);
        double newer = safe(newSalary);
        this.incrementAmount = newer - old;
        this.incrementPercent = old > 0 ? (this.incrementAmount / old) * 100 : 0.0;
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

    private double safe(Double v) { return v != null ? v : 0.0; }
}