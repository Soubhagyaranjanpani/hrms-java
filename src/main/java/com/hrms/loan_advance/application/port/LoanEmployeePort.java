package com.hrms.loan_advance.application.port;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Everything the loan module needs from OTHER modules (employee, payroll, logged-in user).
 *
 * Today {@link com.hrms.loan_advance.infrastructure.LoanEmployeeAdapter} implements this with local
 * repositories. If the loan module becomes its own microservice, only that adapter is replaced
 * (for example with a REST/Feign client); LoanAdvanceService does not change.
 */
public interface LoanEmployeePort {

    /** The logged-in user. */
    LoanEmployee currentEmployee();

    /** By numeric employees.id. */
    Optional<LoanEmployee> findById(Long employeeId);

    /** By employee code (what loan.emp_id stores). */
    Optional<LoanEmployee> findByCode(String employeeCode);

    /** Batch lookup so lists need one query, not one per row. Key = employee code. */
    Map<String, LoanEmployee> findByCodes(Collection<String> employeeCodes);

    /** Employee codes whose name or code contains the text (used by the Approvals page search). */
    List<String> searchEmployeeCodes(String text);

    /** Latest monthly net salary, empty when the employee has no usable payroll record. */
    Optional<BigDecimal> latestNetSalary(Long employeeId);
}