package com.hrms.master.infrastructure;

import com.hrms.master.domain.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActionTypeRepository extends JpaRepository<ActionType, Long> {

    Optional<ActionType> findByName(String name);

    boolean existsByName(String name);

    List<ActionType> findByIsActiveTrue();
}