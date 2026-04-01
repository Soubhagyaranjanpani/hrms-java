package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.EmployeeServiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeServiceHistoryRepository
        extends JpaRepository<EmployeeServiceHistory, Long> {

    List<EmployeeServiceHistory> findByEmployee_Id(Long employeeId);
}
