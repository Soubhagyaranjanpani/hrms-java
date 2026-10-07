package com.hrms.master.infrastructure;



import com.hrms.master.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByNameAndBranch_Id(String name, Long branchId);

    List<Department> findByIsDeletedFalse();

    List<Department> findByIsActiveTrueAndIsDeletedFalse();


}
