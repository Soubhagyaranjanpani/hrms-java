// File: com/hrms/audit/specification/AuditSpecification.java
package com.hrms.audit.application;

import com.hrms.audit.domain.AuditLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditSpecification implements Specification<AuditLog> {

    private String employeeSearch;
    private String module;
    private String action;
    private String performedBy;
    private LocalDateTime eventTimeFrom;
    private LocalDateTime eventTimeTo;

    @Override
    public Predicate toPredicate(
            jakarta.persistence.criteria.Root<AuditLog> root,
            jakarta.persistence.criteria.CriteriaQuery<?> query,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        // Employee search (name or code)
        if (employeeSearch != null && !employeeSearch.isEmpty()) {
            String searchPattern = "%" + employeeSearch.toLowerCase() + "%";
            predicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("employeeName")), searchPattern),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("employeeCode")), searchPattern)
                    )
            );
        }

        // Module filter
        if (module != null && !module.isEmpty() && !"all".equalsIgnoreCase(module)) {
            predicates.add(
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("module")), module.toLowerCase())
            );
        }

        // Action filter
        if (action != null && !action.isEmpty() && !"all".equalsIgnoreCase(action)) {
            predicates.add(
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("action")), action.toLowerCase())
            );
        }

        // Performed by (user) filter
        if (performedBy != null && !performedBy.isEmpty()) {
            predicates.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("performedBy")),
                            "%" + performedBy.toLowerCase() + "%"
                    )
            );
        }

        // Date range filters
        if (eventTimeFrom != null) {
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventTime"), eventTimeFrom)
            );
        }

        if (eventTimeTo != null) {
            predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventTime"), eventTimeTo)
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    // Builder methods for fluent API
    public AuditSpecification withEmployeeSearch(String employeeSearch) {
        this.employeeSearch = employeeSearch;
        return this;
    }

    public AuditSpecification withModule(String module) {
        this.module = module;
        return this;
    }

    public AuditSpecification withAction(String action) {
        this.action = action;
        return this;
    }

    public AuditSpecification withPerformedBy(String performedBy) {
        this.performedBy = performedBy;
        return this;
    }

    public AuditSpecification withEventTimeFrom(LocalDateTime eventTimeFrom) {
        this.eventTimeFrom = eventTimeFrom;
        return this;
    }

    public AuditSpecification withEventTimeTo(LocalDateTime eventTimeTo) {
        this.eventTimeTo = eventTimeTo;
        return this;
    }
}