package com.hrms.audit.infrastructure;

import com.hrms.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByModuleAndReferenceId(String module, Long referenceId);

    List<AuditLog> findByPerformedBy(String performedBy);

    List<AuditLog> findByEventTimeBetween(LocalDateTime start, LocalDateTime end);
}