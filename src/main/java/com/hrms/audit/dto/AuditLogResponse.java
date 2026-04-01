package com.hrms.audit.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogResponse {

    private String entityName;
    private Long entityId;

    private String action;
    private String performedBy;

    private LocalDateTime timestamp;

    private String oldValue;
    private String newValue;
}
