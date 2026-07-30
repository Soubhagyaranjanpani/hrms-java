package com.hrms.master.infrastructure;

import com.hrms.master.domain.PenaltyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PenaltyTypeRepository extends JpaRepository<PenaltyType, Long> {

    Optional<PenaltyType> findByName(String name);

    boolean existsByName(String name);

    List<PenaltyType> findByIsActiveTrue();
}