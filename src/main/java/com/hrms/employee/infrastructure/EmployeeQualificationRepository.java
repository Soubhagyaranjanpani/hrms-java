package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.EmployeeQualification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeQualificationRepository extends JpaRepository<EmployeeQualification, Long> {

    List<EmployeeQualification> findByEmployee_Id(Long employeeId);
}
