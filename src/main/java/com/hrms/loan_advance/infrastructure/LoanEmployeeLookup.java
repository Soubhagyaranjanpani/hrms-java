package com.hrms.loan_advance.infrastructure;

import com.hrms.employee.domain.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Read-only employee lookups by employee_code (loan.emp_id stores the code).
 * Kept inside this module so EmployeeRepository does not need to change.
 */
public interface LoanEmployeeLookup extends Repository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    List<Employee> findByEmployeeCodeIn(Collection<String> employeeCodes);

    /** Employee codes whose code or full name contains the text (case-insensitive). */
    @Query("select e.employeeCode from Employee e "
            + "where lower(e.employeeCode) like lower(concat('%', :text, '%')) "
            + "or lower(concat(e.firstName, ' ', coalesce(e.lastName, ''))) like lower(concat('%', :text, '%'))")
    List<String> searchEmployeeCodes(@Param("text") String text, Pageable pageable);
}