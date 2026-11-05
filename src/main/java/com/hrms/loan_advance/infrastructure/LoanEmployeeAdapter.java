package com.hrms.loan_advance.infrastructure;

import com.hrms.common.utils.CurrentUser;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.loan_advance.application.port.LoanEmployee;
import com.hrms.loan_advance.application.port.LoanEmployeePort;
import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * In-process implementation of {@link LoanEmployeePort}: the ONLY class in the loan module that
 * touches the employee and payroll modules. Replace it to move the loan module out of this app.
 */
@Component
@RequiredArgsConstructor
public class LoanEmployeeAdapter implements LoanEmployeePort {

    private static final int MAX_SEARCH_RESULTS = 100;

    private final CurrentUser currentUser;
    private final EmployeeRepository employeeRepository;
    private final LoanEmployeeLookup employeeLookup;
    private final PayrollRepository payrollRepository;

    @Override
    public LoanEmployee currentEmployee() {
        return toLoanEmployee(currentUser.getEmployee());
    }

    @Override
    public Optional<LoanEmployee> findById(Long employeeId) {
        return employeeRepository.findByIdAndIsDeletedFalse(employeeId).map(this::toLoanEmployee);
    }

    @Override
    public Optional<LoanEmployee> findByCode(String employeeCode) {
        return employeeLookup.findByEmployeeCode(employeeCode).map(this::toLoanEmployee);
    }

    @Override
    public Map<String, LoanEmployee> findByCodes(Collection<String> employeeCodes) {
        if (employeeCodes == null || employeeCodes.isEmpty()) return Map.of();
        return employeeLookup.findByEmployeeCodeIn(employeeCodes).stream()
                .map(this::toLoanEmployee)
                .collect(Collectors.toMap(LoanEmployee::employeeCode, Function.identity(), (a, b) -> a));
    }

    @Override
    public List<String> searchEmployeeCodes(String text) {
        if (text == null || text.isBlank()) return List.of();
        return employeeLookup.searchEmployeeCodes(text.trim(), PageRequest.of(0, MAX_SEARCH_RESULTS));
    }

    @Override
    public Optional<BigDecimal> latestNetSalary(Long employeeId) {
        List<PayrollRecord> history =
                payrollRepository.findByEmployee_IdAndIsDeletedFalseOrderByYearMonthDesc(employeeId);
        if (history.isEmpty() || history.get(0).getNetSalary() == null) return Optional.empty();
        return Optional.of(BigDecimal.valueOf(history.get(0).getNetSalary()).setScale(2, RoundingMode.HALF_UP));
    }

    private LoanEmployee toLoanEmployee(Employee e) {
        return new LoanEmployee(
                e.getId(),
                e.getEmployeeCode(),
                e.getFullName(),
                e.getDepartment() != null ? e.getDepartment().getName() : null,
                e.getGrade() != null ? e.getGrade().getName() : null,
                e.getManager() != null ? e.getManager().getEmployeeCode() : null);
    }
}