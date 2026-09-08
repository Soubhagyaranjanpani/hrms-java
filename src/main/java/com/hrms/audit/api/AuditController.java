// File: com/hrms/audit/controller/AuditController.java
package com.hrms.audit.api;

import com.hrms.audit.application.GetAuditLogsUseCase;
import com.hrms.audit.dto.AuditFilterRequest;
import com.hrms.audit.dto.AuditLogDTO;
import com.hrms.audit.dto.AuditLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Trail", description = "Audit log management APIs")
@CrossOrigin(origins = "*")
public class AuditController {

    private final GetAuditLogsUseCase getAuditLogsUseCase;

    @PostMapping("/search")
    @Operation(summary = "Search audit logs with filters")
    public ResponseEntity<AuditLogResponse> searchAuditLogs(@RequestBody AuditFilterRequest filterRequest) {
        log.info("Searching audit logs");
        AuditLogResponse response = getAuditLogsUseCase.execute(filterRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get audit log by ID")
    public ResponseEntity<AuditLogDTO> getAuditLogById(@PathVariable Long id) {
        log.info("Fetching audit log with id: {}", id);
        AuditLogDTO auditLog = getAuditLogsUseCase.getAuditLogById(id);
        return ResponseEntity.ok(auditLog);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter audit logs with query parameters")
    public ResponseEntity<AuditLogResponse> filterAuditLogs(
            @RequestParam(required = false) String employeeSearch,
            @RequestParam(required = false, defaultValue = "all") String module,
            @RequestParam(required = false, defaultValue = "all") String action,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "eventTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        AuditFilterRequest filterRequest = AuditFilterRequest.builder()
                .employeeSearch(employeeSearch)
                .module(module)
                .action(action)
                .user(user)
                .dateFrom(dateFrom != null ? java.time.LocalDate.parse(dateFrom) : null)
                .dateTo(dateTo != null ? java.time.LocalDate.parse(dateTo) : null)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        AuditLogResponse response = getAuditLogsUseCase.execute(filterRequest);
        return ResponseEntity.ok(response);
    }
}