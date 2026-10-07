package com.hrms.master.infrastructure;

import com.hrms.master.domain.RevisionReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RevisionReasonRepository extends JpaRepository<RevisionReason, Long> {

    Optional<RevisionReason> findByName(String name);

    boolean existsByName(String name);

    List<RevisionReason> findByIsActiveTrue();
}