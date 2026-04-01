package com.hrms.audit.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityName;
    private Long entityId;

    private String action;

    private String performedBy;

    private LocalDateTime timestamp;

    @Column(length = 5000)
    private String oldValue;

    @Column(length = 5000)
    private String newValue;
}
