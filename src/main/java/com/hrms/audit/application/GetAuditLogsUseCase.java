// File: com/hrms/audit/application/GetAuditLogsUseCase.java
package com.hrms.audit.application;

import com.hrms.audit.dto.AuditFilterRequest;
import com.hrms.audit.dto.AuditLogDTO;
import com.hrms.audit.dto.AuditLogResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAuditLogsUseCase {

    private final AuditLogServiceImpl auditLogService;  // Use implementation directly

    public AuditLogResponse execute(AuditFilterRequest filterRequest) {
        log.debug("Executing GetAuditLogsUseCase");

        return auditLogService.getFilteredAuditLogs(
                filterRequest.getEmployeeSearch(),
                filterRequest.getModule(),
                filterRequest.getAction(),
                filterRequest.getUser(),
                filterRequest.getDateFrom() != null ? filterRequest.getDateFrom().atStartOfDay() : null,
                filterRequest.getDateTo() != null ? filterRequest.getDateTo().atTime(23, 59, 59) : null,
                filterRequest.getPage(),
                filterRequest.getSize(),
                filterRequest.getSortBy(),
                filterRequest.getSortDirection()
        );
    }

    public AuditLogDTO getAuditLogById(Long id) {
        return auditLogService.getAuditLogById(id);
    }
}