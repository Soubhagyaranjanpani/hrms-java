// File: com/hrms/audit/dto/AuditLogDTO.java
package com.hrms.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    private Long id;
    private String auditId;
    private LocalDateTime eventTime;
    private String performedBy;
    private String performedByRole;
    private String module;
    private String action;
    private String description;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String fieldChanged;
    private String oldValue;
    private String newValue;
    private String remarks;
    private String ipAddress;
    private String device;
    private Long referenceId;
}