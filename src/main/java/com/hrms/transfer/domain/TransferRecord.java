package com.hrms.transfer.domain;

import com.hrms.employee.domain.Employee;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "transfer_records")
public class TransferRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "transfer_order_number", unique = true, length = 100)
    private String transferOrderNumber;

    @Column(name = "transfer_date")
    private LocalDate transferDate;

    // Stored as plain string since the form uses a simple dropdown
    // (Permanent Transfer / Temporary / On Deputation) with no dedicated master table.
    @Column(name = "transfer_type", length = 40)
    private String transferType;

    // ── Department change (From → To) ──
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_department", nullable = false)
    private Department fromDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_department", nullable = false)
    private Department toDepartment;

    // ── Branch change (From → To) ──
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_branch", nullable = false)
    private Branch fromBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_branch", nullable = false)
    private Branch toBranch;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "transfer_reason", length = 1000)
    private String transferReason;

    // Snapshot of the employee's designation at transfer time — the form shows
    // this as a read-only "Auto-populated" field; a transfer doesn't change designation.
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
