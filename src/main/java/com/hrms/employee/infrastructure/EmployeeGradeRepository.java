// File: com/hrms/employee/infrastructure/EmployeeGradeRepository.java
package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.EmployeeGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EmployeeGradeRepository extends JpaRepository<EmployeeGrade, Long> {

    Optional<EmployeeGrade> findByCodeAndIsActiveTrue(String code);

    List<EmployeeGrade> findByIsActiveTrueOrderByLevelAsc();

    List<EmployeeGrade> findByIsActiveTrue();

    boolean existsByCode(String code);
}