package com.hrms.employment_type.infrastructure;

import com.hrms.employment_type.domain.EmploymentType;   // correct import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmploymentTypeRepository extends JpaRepository<EmploymentType, Long> {
    boolean existsByName(String name);
    List<EmploymentType> findByIsActiveTrue();
}