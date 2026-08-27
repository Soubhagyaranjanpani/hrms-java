package com.hrms.audit.application;

import com.hrms.audit.domain.AuditLog;
import com.hrms.audit.infrastructure.AuditLogRepository;
import com.hrms.employee.domain.Employee;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Generic audit method — call this from ANY module's service
     * (Promotion, Transfer, ServiceBook, etc.) after an action succeeds.
     */
    public void log(String module, String action, String description,
                    Employee subjectEmployee, String fieldChanged,
                    String oldValue, String newValue, String remarks,
                    Long referenceId) {

        AuditLog log = new AuditLog();
        log.setModule(module);
        log.setAction(action);
        log.setDescription(description);
        log.setFieldChanged(fieldChanged);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setRemarks(remarks);
        log.setReferenceId(referenceId);

        if (subjectEmployee != null) {
            log.setEmployeeId(subjectEmployee.getId());
            log.setEmployeeName(subjectEmployee.getFullName());
            log.setEmployeeCode(subjectEmployee.getEmployeeCode());
        }

        populatePerformedBy(log);
        populateRequestContext(log);

        auditLogRepository.save(log);
    }

    /** Shorthand for simple actions with no field diff (Create/View/Approval/etc.) */
    public void log(String module, String action, String description,
                    Employee subjectEmployee, Long referenceId) {
        log(module, action, description, subjectEmployee, null, null, null, null, referenceId);
    }

    /**
     * Overload for callers that only have the employee ID on hand (not the full
     * Employee entity) and want to log a field/status change using any value
     * type (e.g. enums like LeaveStatus) — oldValue/newValue are converted via
     * toString(). Employee name/code will NOT be populated on the audit row
     * since only the ID is available here.
     */
    public void log(String module, Long employeeId, String fieldChanged,
                    String description, Object oldValue, Object newValue) {

        AuditLog log = new AuditLog();
        log.setModule(module);
        log.setAction("Update");
        log.setDescription(description);
        log.setEmployeeId(employeeId);
        log.setFieldChanged(fieldChanged);
        log.setOldValue(oldValue != null ? oldValue.toString() : null);
        log.setNewValue(newValue != null ? newValue.toString() : null);

        populatePerformedBy(log);
        populateRequestContext(log);

        auditLogRepository.save(log);
    }

    // ── Convenience wrapper specifically for Service Book ──
    public void logServiceBookView(Employee subjectEmployee, Long serviceBookId) {
        log("Service Book", "View",
                "Service Book Viewed for " + subjectEmployee.getFullName(),
                subjectEmployee, serviceBookId);
    }

    public void logServiceBookDownload(Employee subjectEmployee, Long serviceBookId, String fileName) {
        log("Service Book", "Download",
                "Service Book PDF Downloaded",
                subjectEmployee, "Document", "--", fileName,
                "Service book downloaded", serviceBookId);
    }

    private void populatePerformedBy(AuditLog log) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Employee currentUser) {
            log.setPerformedBy(currentUser.getFullName());
            if (currentUser.getRole() != null) {
                log.setPerformedByRole(currentUser.getRole().getName());
            }
        }
    }

    private void populateRequestContext(AuditLog log) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        log.setIpAddress(ip != null ? ip.split(",")[0] : request.getRemoteAddr());
        log.setDevice(request.getHeader("User-Agent"));
    }
}