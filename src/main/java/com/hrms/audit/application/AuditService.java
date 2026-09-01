package com.hrms.audit.application;

import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditLog;
import com.hrms.audit.domain.AuditModule;
import com.hrms.audit.infrastructure.AuditLogRepository;
import com.hrms.employee.domain.Employee;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private static final AtomicLong AUDIT_SEQUENCE = new AtomicLong(0);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * Generic audit method — call this from ANY module's service
     * (Promotion, Transfer, ServiceBook, etc.) after an action succeeds.
     */
    public void log(String module, String action, String description,
                    Employee subjectEmployee, String fieldChanged,
                    String oldValue, String newValue, String remarks,
                    Long referenceId) {

        AuditLog log = new AuditLog();
        log.setAuditId(generateAuditId());
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


    // Add to AuditAction class
    public static final String LOGIN = "Login";
    public static final String LOGOUT = "Logout";

    // Add to AuditModule class (if you want a separate AUTH module)
    public static final String AUTH = "Authentication";

    // Add to AuditService class
    public void logLogin(Employee subjectEmployee, String status) {
        log(AuditModule.AUTH,
                AuditAction.LOGIN,
                "User login " + status + ": " + subjectEmployee.getFullName() +
                        " (" + subjectEmployee.getEmployeeCode() + ")",
                subjectEmployee,
                "Login",
                null,
                status,
                "User authentication event",
                subjectEmployee.getId());
    }

    public void logLogout(Employee subjectEmployee) {
        log(AuditModule.AUTH,
                AuditAction.LOGOUT,
                "User logged out: " + subjectEmployee.getFullName(),
                subjectEmployee,
                subjectEmployee.getId());
    }

    /** Shorthand for simple actions with no field diff (View/Approval/etc.) */
    public void log(String module, String action, String description,
                    Employee subjectEmployee, Long referenceId) {
        log(module, action, description, subjectEmployee, null, null, null, null, referenceId);
    }

    /** Special method for CREATE operations - no old value, only new value */
    public void logCreate(String module, String description,
                          Employee subjectEmployee, String newValue,
                          String remarks, Long referenceId) {
        log(module, AuditAction.CREATE, description, subjectEmployee,
                "New Record", null, newValue, remarks, referenceId);
    }

    // ── Convenience wrappers specifically for Service Book ──
    public void logServiceBookView(Employee subjectEmployee, Long serviceBookId) {
        log(AuditModule.SERVICE_BOOK, AuditAction.VIEW,
                "Service Book Viewed for " + subjectEmployee.getFullName(),
                subjectEmployee, serviceBookId);
    }

    public void logServiceBookDownload(Employee subjectEmployee, Long serviceBookId, String fileName) {
        log(AuditModule.SERVICE_BOOK, AuditAction.DOWNLOAD,
                "Service Book PDF Downloaded",
                subjectEmployee, "Document", null, fileName,
                "Service book downloaded", serviceBookId);
    }

    // ── Convenience wrappers for Employee ──
    public void logEmployeeCreate(Employee subjectEmployee) {
        logCreate(AuditModule.EMPLOYEE,
                "Employee created: " + subjectEmployee.getFullName() +
                        " (" + subjectEmployee.getEmployeeCode() + ")",
                subjectEmployee,
                subjectEmployee.getFullName(),
                "New employee created with role: " +
                        (subjectEmployee.getRole() != null ? subjectEmployee.getRole().getName() : "N/A"),
                subjectEmployee.getId());
    }

    public void logEmployeeUpdate(Employee subjectEmployee, String fieldChanged,
                                  String oldValue, String newValue) {
        log(AuditModule.EMPLOYEE, AuditAction.UPDATE,
                "Employee updated: " + subjectEmployee.getFullName(),
                subjectEmployee, fieldChanged, oldValue, newValue,
                "Employee record updated", subjectEmployee.getId());
    }

    // ── Convenience wrappers for Promotion ──
    public void logPromotionCreate(Employee subjectEmployee, Long promotionId) {
        logCreate(AuditModule.PROMOTION,
                "Promotion created for " + subjectEmployee.getFullName(),
                subjectEmployee,
                "New Promotion",
                "Promotion record created",
                promotionId);
    }

    public void logPromotionApproval(Employee subjectEmployee, Long promotionId, String remarks) {
        log(AuditModule.PROMOTION, AuditAction.APPROVAL,
                "Promotion approved for " + subjectEmployee.getFullName(),
                subjectEmployee, "Status", "Pending", "Approved",
                remarks, promotionId);
    }

    // ── Convenience wrappers for Transfer ──
    public void logTransferCreate(Employee subjectEmployee, Long transferId) {
        logCreate(AuditModule.TRANSFER,
                "Transfer created for " + subjectEmployee.getFullName(),
                subjectEmployee,
                "New Transfer",
                "Transfer record created",
                transferId);
    }

    // ── Convenience wrappers for Documents ──
    public void logDocumentUpload(Employee subjectEmployee, Long documentId, String fileName) {
        log(AuditModule.DOCUMENTS, AuditAction.UPLOAD,
                "Document uploaded: " + fileName,
                subjectEmployee, "Document", null, fileName,
                "Document uploaded to system", documentId);
    }

    // ── Audit ID Generator ──
    private String generateAuditId() {
        long sequence = AUDIT_SEQUENCE.incrementAndGet();
        if (sequence > 999999) {
            AUDIT_SEQUENCE.set(0);
            sequence = AUDIT_SEQUENCE.incrementAndGet();
        }
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        return String.format("AUD-%s-%06d", timestamp, sequence);
    }

    private void populatePerformedBy(AuditLog log) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Employee currentUser) {
            log.setPerformedBy(currentUser.getFullName());
            if (currentUser.getRole() != null) {
                log.setPerformedByRole(currentUser.getRole().getName());
            }
        } else if (auth != null && auth.getName() != null) {
            log.setPerformedBy(auth.getName());
        }
    }

    private void populateRequestContext(AuditLog log) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            log.setIpAddress(ip.split(",")[0].trim());
        } else {
            log.setIpAddress(request.getRemoteAddr());
        }
        log.setDevice(request.getHeader("User-Agent"));
    }
}