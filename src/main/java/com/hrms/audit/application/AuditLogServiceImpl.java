package com.hrms.audit.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrms.audit.domain.AuditLog;
import com.hrms.audit.infrastructure.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditRepo;
    private final ObjectMapper objectMapper;
    @Async
    @Override
    public void log(String entity,
                    Long entityId,
                    String action,
                    String performedBy,
                    Object oldValue,
                    Object newValue) {

        AuditLog log = new AuditLog();

        log.setEntityName(entity);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setPerformedBy(performedBy);
        log.setTimestamp(LocalDateTime.now());

        try {
            if (oldValue != null) {
                log.setOldValue(objectMapper.writeValueAsString(oldValue));
            }
            if (newValue != null) {
                log.setNewValue(objectMapper.writeValueAsString(newValue));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing audit log", e);
        }

        try {
            auditRepo.save(log);
        } catch (Exception e) {
            // DO NOT break main flow
            System.err.println("Audit failed: " + e.getMessage());
        }
    }
}
