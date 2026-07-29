package com.hrms.payrevision.domain;

import com.hrms.employee.domain.Employee;
import com.hrms.master.domain.RevisionReason;  // ← Master se import
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "pay_revision_records")
public class PayRevisionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_revision_order_number", unique = true, length = 100)
    private String payRevisionOrderNumber;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "previous_pay_scale_min")
    private Double previousPayScaleMin;

    @Column(name = "previous_pay_scale_max")
    private Double previousPayScaleMax;

    @Column(name = "revised_pay_scale_min")
    private Double revisedPayScaleMin;

    @Column(name = "revised_pay_scale_max")
    private Double revisedPayScaleMax;

    private Double incrementAmount = 0.0;
    private Double incrementPercent = 0.0;

    // ✅ Master RevisionReason use karo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason", nullable = false)
    private RevisionReason reason;  // ← Master package ka

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

    public void compute() {
        double prevMin = safe(previousPayScaleMin);
        double revMin = safe(revisedPayScaleMin);
        this.incrementAmount = revMin - prevMin;
        this.incrementPercent = prevMin > 0 ? (this.incrementAmount / prevMin) * 100 : 0.0;
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