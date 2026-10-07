package com.hrms.master.infrastructure;

import com.hrms.master.domain.PensionEligibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PensionEligibilityRepository extends JpaRepository<PensionEligibility, Long> {

    Optional<PensionEligibility> findByName(String name);

    boolean existsByName(String name);

    List<PensionEligibility> findByIsActiveTrue();
}