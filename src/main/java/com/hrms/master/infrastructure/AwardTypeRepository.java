package com.hrms.master.infrastructure;

import com.hrms.master.domain.AwardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AwardTypeRepository extends JpaRepository<AwardType, Long> {

    Optional<AwardType> findByName(String name);

    boolean existsByName(String name);

    List<AwardType> findByIsActiveTrue();
}