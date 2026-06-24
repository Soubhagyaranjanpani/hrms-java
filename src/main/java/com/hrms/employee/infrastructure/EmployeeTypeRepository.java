package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeTypeRepository
        extends JpaRepository<EmployeeType, Long> {

    List<EmployeeType> findByIsDeleted(Boolean isDeleted);
}