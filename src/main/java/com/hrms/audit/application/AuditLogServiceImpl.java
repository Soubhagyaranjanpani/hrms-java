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
public class AuditLogServiceImpl implements AuditLogService {  // ✅ implements restored

    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(String entity, Long entityId, String action,
                    String performedBy, Object oldValue, Object newValue) {

        AuditLog auditLog = new AuditLog();
        auditLog.setModule(entity);
        auditLog.setReferenceId(entityId);
        auditLog.setAction(action);
        auditLog.setPerformedBy(performedBy);
        auditLog.setOldValue(oldValue != null ? oldValue.toString() : null);
        auditLog.setNewValue(newValue != null ? newValue.toString() : null);

        auditLogRepository.save(auditLog);

        log.info("Audit log created for entity: {}, action: {}, performedBy: {}",
                entity, action, performedBy);
    }

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

        AuditSpecification spec = new AuditSpecification();

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

        Sort.Direction direction = Sort.Direction.fromString(
                sortDirection != null ? sortDirection : "DESC"
        );

        String sortByField = sortBy != null ? sortBy : "eventTime";
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortByField));

        Page<AuditLog> auditLogPage = auditLogRepository.findAll(spec, pageable);

        List<AuditLogDTO> dtoList = auditLogPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

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

    public AuditLogDTO getAuditLogById(Long id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit log not found with id: " + id));
        return convertToDTO(auditLog);
    }

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