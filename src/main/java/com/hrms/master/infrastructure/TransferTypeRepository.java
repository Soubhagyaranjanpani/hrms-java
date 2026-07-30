package com.hrms.master.infrastructure;

import com.hrms.master.domain.TransferType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransferTypeRepository extends JpaRepository<TransferType, Long> {

    Optional<TransferType> findByName(String name);

    boolean existsByName(String name);

    List<TransferType> findByIsActiveTrue();
}