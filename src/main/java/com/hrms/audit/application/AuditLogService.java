package com.hrms.audit.application;

public interface AuditLogService {

    void log(String entity,
             Long entityId,
             String action,
             String performedBy,
             Object oldValue,
             Object newValue);
}
