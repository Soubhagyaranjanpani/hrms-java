package com.hrms.employee.infrastructure;



import com.hrms.employee.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByPhone(String phone);

    Optional<Employee> findByEmailAndIsActiveTrue(String email);

    Optional<Employee> findByPhoneAndIsActiveTrue(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    // 🔥 Role-based resolution (HR / ADMIN)
    Optional<Employee> findFirstByRole_Name(String roleName);

    List<Employee> findByRole_Name(String roleName);

    // 👨‍💼 Manager hierarchy
    List<Employee> findByManager(Employee manager);

    List<Employee> findByManager_Id(Long managerId);

    // 👥 Active employees
    List<Employee> findByIsActiveTrueAndIsDeletedFalse();

    // 🔍 Search
    List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}
