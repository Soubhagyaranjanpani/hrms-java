package com.hrms.master.infrastructure;

import com.hrms.master.domain.RetirementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RetirementTypeRepository extends JpaRepository<RetirementType, Long> {

    Optional<RetirementType> findByName(String name);

    boolean existsByName(String name);

    List<RetirementType> findByIsActiveTrue();
}