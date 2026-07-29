package com.hrms.master.infrastructure;

import com.hrms.master.domain.DeputationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeputationTypeRepository extends JpaRepository<DeputationType, Long> {

    Optional<DeputationType> findByName(String name);

    boolean existsByName(String name);

    List<DeputationType> findByIsActiveTrue();
}