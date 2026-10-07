package com.hrms.payroll.infrastructure;



import com.hrms.payroll.domain.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, Long> {

    Optional<SalaryStructure> findByEmployee_IdAndIsActiveTrue(Long empId);

    List<SalaryStructure> findByIsActiveTrue();
}
