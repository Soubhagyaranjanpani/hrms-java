package com.hrms.repository;



import com.hrms.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByPhone(String phone);

    Optional<Employee> findByEmailAndIsActiveTrue(String email);

    Optional<Employee> findByPhoneAndIsActiveTrue(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
