package com.hrms.audit.api;

import com.hrms.audit.application.GetAuditLogsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final GetAuditLogsUseCase useCase;

    @GetMapping("/entity")
    public Object getByEntity(
            @RequestParam String entity,
            @RequestParam Long entityId) {

        return useCase.getByEntity(entity, entityId);
    }

    @GetMapping("/user")
    public Object getByUser(@RequestParam String user) {

        return useCase.getByUser(user);
    }

    @GetMapping("/date-range")
    public Object getByDateRange(
            @RequestParam String start,
            @RequestParam String end) {

        return useCase.getByDateRange(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );
    }
}
