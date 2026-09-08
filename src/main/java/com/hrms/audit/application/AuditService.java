package com.hrms.audit.application;

import com.hrms.audit.domain.AuditLog;
import com.hrms.audit.dto.AuditLogDTO;
import com.hrms.audit.dto.AuditLogResponse;
import com.hrms.audit.repository.AuditLogRepository;
import com.hrms.audit.application.AuditSpecification;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.employee.domain.Employee;
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
public class AuditService {

    private final AuditLogRepository auditLogRepository;

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

    public AuditLogDTO getAuditLogById(Long id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit log not found with id: " + id));
        return convertToDTO(auditLog);
    }

    @Transactional
    public void saveAuditLog(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }

    // ✅ METHOD 1: 5-parameter wala log method
    @Transactional
    public void log(String employeeSearch, String module, String action,
                    Employee employee, Long referenceId) {

        AuditLog auditLog = new AuditLog();

        auditLog.setEmployeeName(employeeSearch);
        auditLog.setEmployeeCode(employeeSearch);

        if (employee != null) {
            auditLog.setEmployeeId(employee.getId());
        }

        auditLog.setModule(module);
        auditLog.setAction(action);
        auditLog.setPerformedBy(employeeSearch);
        auditLog.setReferenceId(referenceId);

        auditLogRepository.save(auditLog);

        log.info("Audit log created for employee: {}, module: {}, action: {}", employeeSearch, module, action);
    }

    // ✅ METHOD 2: String wala 9-parameter log method
    @Transactional
    public void log(String employeeSearch, String module, String action,
                    Employee employee, String performedBy,
                    String description, String remarks, String ipAddress, Long referenceId) {

        AuditLog auditLog = new AuditLog();

        if (employee != null) {
            auditLog.setEmployeeId(employee.getId());
            auditLog.setEmployeeName(employeeSearch);
            auditLog.setEmployeeCode(employeeSearch);
        }

        auditLog.setModule(module);
        auditLog.setAction(action);
        auditLog.setPerformedBy(performedBy);
        auditLog.setDescription(description);
        auditLog.setRemarks(remarks);
        auditLog.setIpAddress(ipAddress);
        auditLog.setReferenceId(referenceId);

        auditLogRepository.save(auditLog);

        log.info("Audit log created for employee: {}, module: {}, action: {}", employeeSearch, module, action);
    }

    // ✅ METHOD 3: Enum wala 9-parameter log method
    @Transactional
    public void log(AuditModule module, AuditAction action, String description,
                    Employee employee, String objectType, String oldValue,
                    String newValue, String remarks, Long referenceId) {

        AuditLog auditLog = new AuditLog();

        // Module aur Action ko Enum se String mein convert karein
        auditLog.setModule(module.toString());
        auditLog.setAction(action.toString());

        auditLog.setDescription(description);

        if (employee != null) {
            auditLog.setEmployeeId(employee.getId());
            auditLog.setEmployeeName(employee.toString());
            auditLog.setEmployeeCode(employee.toString());
        }

        auditLog.setPerformedBy(employee != null ? employee.toString() : null);

        // DTO se Data set karein
        if (objectType != null) {
            auditLog.setFieldChanged(objectType);
        }
        if (oldValue != null) {
            auditLog.setOldValue(oldValue);
        }
        if (newValue != null) {
            auditLog.setNewValue(newValue);
        }
        if (remarks != null) {
            auditLog.setRemarks(remarks);
        }

        auditLog.setReferenceId(referenceId);

        auditLogRepository.save(auditLog);

        log.info("Audit log created for employee: {}, module: {}, action: {}", employee != null ? employee.toString() : "Unknown", module, action);
    }

    // ✅ METHOD 4: logLogin method (Naya add kiya)
    @Transactional
    public void logLogin(Employee employee, String ipAddress) {

        AuditLog auditLog = new AuditLog();

        auditLog.setModule("AUTH");
        auditLog.setAction("LOGIN");
        auditLog.setPerformedBy(employee != null ? employee.toString() : null);
        auditLog.setDescription("User logged in");
        auditLog.setIpAddress(ipAddress);

        if (employee != null) {
            auditLog.setEmployeeId(employee.getId());
            auditLog.setEmployeeName(employee.toString());
        }

        auditLog.setReferenceId(employee != null ? employee.getId() : null);

        auditLogRepository.save(auditLog);

        log.info("Login audit log created for employee: {}, ip: {}", employee != null ? employee.toString() : "Unknown", ipAddress);
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