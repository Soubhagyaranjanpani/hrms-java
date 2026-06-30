package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.EmployeeConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeConfirmationRepository
        extends JpaRepository<EmployeeConfirmation, Long> {

    List<EmployeeConfirmation> findByIsDeletedFalse();

    List<EmployeeConfirmation> findByIsDeletedTrue();
}