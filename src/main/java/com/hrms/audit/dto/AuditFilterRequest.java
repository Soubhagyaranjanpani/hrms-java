// File: com/hrms/audit/dto/AuditFilterRequest.java
package com.hrms.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditFilterRequest {
    private String employeeSearch;
    private String module;
    private String action;
    private String user;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private int page = 0;
    private int size = 8;
    private String sortBy = "eventTime";
    private String sortDirection = "DESC";
}