// File: com/hrms/audit/application/AuditLogServiceImpl.java
package com.hrms.audit.application;

import com.hrms.audit.domain.AuditLog;
import com.hrms.audit.dto.AuditLogDTO;
import com.hrms.audit.dto.AuditLogResponse;
import com.hrms.audit.repository.AuditLogRepository;
import com.hrms.audit.application.AuditSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogServiceImpl {  // Removed "implements AuditLogService"

    private final AuditLogRepository auditLogRepository;

    // Removed @Override
    public AuditLogResponse getFilteredAuditLogs(
            String employeeSearch,
            String module,
            String action,
            String user,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        log.debug("Getting filtered audit logs");

        // Build specification
        AuditSpecification spec = new AuditSpecification();

        // Apply filters
        if (employeeSearch != null && !employeeSearch.isEmpty()) {
            spec.withEmployeeSearch(employeeSearch);
        }

        if (module != null && !module.isEmpty() && !"all".equalsIgnoreCase(module)) {
            spec.withModule(module);
        }

        if (action != null && !action.isEmpty() && !"all".equalsIgnoreCase(action)) {
            spec.withAction(action);
        }

        if (user != null && !user.isEmpty()) {
            spec.withPerformedBy(user);
        }

        if (dateFrom != null) {
            spec.withEventTimeFrom(dateFrom);
        }

        if (dateTo != null) {
            spec.withEventTimeTo(dateTo);
        }

        // Create pageable
        Sort.Direction direction = Sort.Direction.fromString(
                sortDirection != null ? sortDirection : "DESC"
        );

        String sortByField = sortBy != null ? sortBy : "eventTime";
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortByField)
        );

        // Execute query
        Page<AuditLog> auditLogPage = auditLogRepository.findAll(spec, pageable);

        // Convert to DTOs
        List<AuditLogDTO> dtoList = auditLogPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Build response
        return AuditLogResponse.builder()
                .content(dtoList)
                .page(auditLogPage.getNumber())
                .size(auditLogPage.getSize())
                .totalElements(auditLogPage.getTotalElements())
                .totalPages(auditLogPage.getTotalPages())
                .first(auditLogPage.isFirst())
                .last(auditLogPage.isLast())
                .build();
    }

    // Removed @Override
    public AuditLogDTO getAuditLogById(Long id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit log not found with id: " + id));
        return convertToDTO(auditLog);
    }

    // Removed @Override
    @Transactional
    public void saveAuditLog(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }

    private AuditLogDTO convertToDTO(AuditLog auditLog) {
        return AuditLogDTO.builder()
                .id(auditLog.getId())
                .auditId(auditLog.getAuditId())
                .eventTime(auditLog.getEventTime())
                .performedBy(auditLog.getPerformedBy())
                .performedByRole(auditLog.getPerformedByRole())
                .module(auditLog.getModule())
                .action(auditLog.getAction())
                .description(auditLog.getDescription())
                .employeeId(auditLog.getEmployeeId())
                .employeeName(auditLog.getEmployeeName())
                .employeeCode(auditLog.getEmployeeCode())
                .fieldChanged(auditLog.getFieldChanged())
                .oldValue(auditLog.getOldValue())
                .newValue(auditLog.getNewValue())
                .remarks(auditLog.getRemarks())
                .ipAddress(auditLog.getIpAddress())
                .device(auditLog.getDevice())
                .referenceId(auditLog.getReferenceId())
                .build();
    }
}