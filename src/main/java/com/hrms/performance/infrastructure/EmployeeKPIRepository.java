package com.hrms.performance.infrastructure;

import com.hrms.performance.domain.EmployeeKPI;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeKPIRepository extends JpaRepository<EmployeeKPI, Long> {

    List<EmployeeKPI> findByEmployee_Id(Long employeeId);
}
