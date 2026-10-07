package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.EmployeeDesignation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeDesignationRepository
        extends JpaRepository<EmployeeDesignation, Long> {
    @EntityGraph(attributePaths = {"employee", "designation"})
    List<EmployeeDesignation> findByIsDeletedFalse();

    @EntityGraph(attributePaths = {"employee", "designation"})
    List<EmployeeDesignation> findByIsActiveTrueAndIsDeletedFalse();

    Optional<EmployeeDesignation> findFirstByEmployee_IdAndIsActiveTrueAndIsDeletedFalse(Long employeeId);
}