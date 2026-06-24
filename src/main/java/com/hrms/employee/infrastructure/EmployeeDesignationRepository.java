package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.EmployeeDesignation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeDesignationRepository
        extends JpaRepository<EmployeeDesignation, Long> {

    // 🔹 Return all non-deleted records
    List<EmployeeDesignation> findByIsDeletedFalse();

    // 🔹 Return only active AND non-deleted records
    List<EmployeeDesignation> findByIsActiveTrueAndIsDeletedFalse();
}