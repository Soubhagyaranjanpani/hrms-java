package com.hrms.audit.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_module", columnList = "module"),
        @Index(name = "idx_audit_action", columnList = "action"),
        @Index(name = "idx_audit_employee", columnList = "employee_id"),
        @Index(name = "idx_audit_reference", columnList = "reference_id"),
        @Index(name = "idx_audit_event_time", columnList = "event_time")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "audit_id", nullable = false, unique = true, length = 50)
    private String auditId; // e.g. AUD-20240101120000-000001

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    // ── Who did it ──
    @Column(name = "performed_by", length = 100)
    private String performedBy;      // user.getFullName()

    @Column(name = "performed_by_role", length = 50)
    private String performedByRole;  // role.getName()

    // ── What / where ──
    @Column(nullable = false, length = 50)
    private String module;   // "Service Book", "Promotion", "Transfer" ...

    @Column(nullable = false, length = 30)
    private String action;   // Create, Update, Approval, Reject, Delete, View, Upload, Download

    @Column(length = 500)
    private String description;

    // ── On whom ──
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_name", length = 100)
    private String employeeName;

    @Column(name = "employee_code", length = 20)
    private String employeeCode;

    // ── Field-level change (Update actions) ──
    @Column(name = "field_changed", length = 100)
    private String fieldChanged;

    @Column(name = "old_value", length = 500)
    private String oldValue;

    @Column(name = "new_value", length = 500)
    private String newValue;

    @Column(length = 1000)
    private String remarks;

    // ── Traceability ──
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(length = 255)
    private String device;

    // The row id of the actual record in the source module table
    // (promotion_records.id, transfer_records.id, service_book row id, etc.)
    @Column(name = "reference_id")
    private Long referenceId;

    @PrePersist
    public void prePersist() {
        if (this.eventTime == null) {
            this.eventTime = LocalDateTime.now();
        }
        // Only generate if not already set by AuditService
        if (this.auditId == null || this.auditId.isEmpty()) {
            this.auditId = "AUD-" + System.currentTimeMillis() + "-" +
                    String.format("%06d", (int)(Math.random() * 1000000));
        }
    }
}