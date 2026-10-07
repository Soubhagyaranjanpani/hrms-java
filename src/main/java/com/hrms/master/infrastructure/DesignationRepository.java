package com.hrms.master.infrastructure;

import com.hrms.master.domain.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

    Optional<Designation> findByName(String name);

    boolean existsByName(String name);

    List<Designation> findByIsActiveTrue();
}
