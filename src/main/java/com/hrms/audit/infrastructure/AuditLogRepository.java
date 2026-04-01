package com.hrms.audit.infrastructure;

import com.hrms.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByEntityNameAndEntityId(String entityName, Long entityId);

    List<AuditLog> findByPerformedBy(String performedBy);

    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
