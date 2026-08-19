package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.EmployeeCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeCertificationRepository extends JpaRepository<EmployeeCertification, Long> {
    List<EmployeeCertification> findByEmployee_Id(Long employeeId);
}