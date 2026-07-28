package com.hrms.master.infrastructure;

import com.hrms.master.domain.RevisionReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevisionReasonRepository extends JpaRepository<RevisionReason, Long> {
    boolean existsByNameIgnoreCase(String name);
}