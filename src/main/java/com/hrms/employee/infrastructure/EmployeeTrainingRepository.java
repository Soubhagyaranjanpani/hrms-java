package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.EmployeeTraining;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeTrainingRepository extends JpaRepository<EmployeeTraining, Long> {

    List<EmployeeTraining> findByEmployee_Id(Long employeeId);
}
