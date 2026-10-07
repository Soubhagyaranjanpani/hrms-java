package com.hrms.retirement.domain;

import com.hrms.employee.domain.Employee;
// ── ASSUMPTION: adjust these two imports if your existing master classes live
// elsewhere or are named differently — the fields below just need a valid FK target.
import com.hrms.master.domain.RetirementType;
import com.hrms.master.domain.PensionEligibility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "retirement_records")
public class RetirementRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "retirement_date")
    private LocalDate retirementDate;

    // ── Both dropdowns map to existing master tables ──
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retirement_type", nullable = false)
    private RetirementType retirementType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_eligibility", nullable = false)
    private PensionEligibility pensionEligibility;

    @Column(name = "pension_number", length = 100)
    private String pensionNumber; // optional — e.g. blank while eligibility is "Pending"

    @Column(name = "retirement_order", length = 100)
    private String retirementOrder;

    @Column(name = "retirement_benefits", length = 1000)
    private String retirementBenefits;

    // Snapshot of department/designation at the time of the record — the form shows
    // these as read-only "Auto-populated" fields.
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
