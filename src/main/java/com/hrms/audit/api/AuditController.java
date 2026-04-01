package com.hrms.audit.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.audit.application.GetAuditLogsUseCase;
import com.hrms.audit.dto.AuditLogResponse;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final GetAuditLogsUseCase useCase;

    @Operation(summary = "Get audit logs by entity")
    @GetMapping("/entity")
    public ApiResponse<List<AuditLogResponse>> getByEntity(
            @RequestParam String entity,
            @RequestParam Long entityId) {

        List<AuditLogResponse> data = useCase.getByEntity(entity, entityId);

        return ResponseUtils.createSuccessResponse(
                data,
                new TypeReference<List<AuditLogResponse>>() {}
        );
    }

    @Operation(summary = "Get audit logs by user")
    @GetMapping("/user")
    public ApiResponse<List<AuditLogResponse>> getByUser(
            @RequestParam String user) {

        List<AuditLogResponse> data = useCase.getByUser(user);

        return ResponseUtils.createSuccessResponse(
                data,
                new TypeReference<List<AuditLogResponse>>() {}
        );
    }

    @Operation(summary = "Get audit logs by date range")
    @GetMapping("/date-range")
    public ApiResponse<List<AuditLogResponse>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<AuditLogResponse> data = useCase.getByDateRange(start, end);

        return ResponseUtils.createSuccessResponse(
                data,
                new TypeReference<List<AuditLogResponse>>() {}
        );
    }
}
