package com.hrms.audit.application;

import com.hrms.audit.domain.AuditLog;
import com.hrms.audit.dto.AuditLogResponse;
import com.hrms.audit.infrastructure.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAuditLogsUseCase {

    private final AuditLogRepository auditRepo;

    public List<AuditLogResponse> getByEntity(String entity, Long entityId) {

        return auditRepo.findByModuleAndReferenceId(entity, entityId)
                .stream().map(this::map).collect(Collectors.toList());
    }

    public List<AuditLogResponse> getByUser(String user) {

        return auditRepo.findByPerformedBy(user)
                .stream().map(this::map).collect(Collectors.toList());
    }

    public List<AuditLogResponse> getByDateRange(LocalDateTime start, LocalDateTime end) {

        return auditRepo.findByEventTimeBetween(start, end)
                .stream().map(this::map).collect(Collectors.toList());
    }

    private AuditLogResponse map(AuditLog log) {

        AuditLogResponse res = new AuditLogResponse();

        res.setEntityName(log.getModule());
        res.setEntityId(log.getReferenceId());
        res.setAction(log.getAction());
        res.setPerformedBy(log.getPerformedBy());
        res.setTimestamp(log.getEventTime());
        res.setOldValue(log.getOldValue());
        res.setNewValue(log.getNewValue());

        return res;
    }
}