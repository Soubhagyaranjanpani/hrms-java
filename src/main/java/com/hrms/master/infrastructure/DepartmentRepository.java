package com.hrms.master.infrastructure;



import com.hrms.master.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);

    Optional<Department> findByNameAndIsActiveTrue(String name);


}
