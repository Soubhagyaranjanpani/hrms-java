package com.hrms.loan_advance.application;

import com.hrms.loan_advance.dto.LoanEligibilityCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Builds the three lines of the approver's "Eligibility Check" box. Pure logic, no database access. */
@Component
@RequiredArgsConstructor
public class LoanEligibilityChecker {

    private final LoanEmiCalculator calculator;

    /**
     * @param netSalary    latest monthly net salary, empty if the employee has no payroll
     * @param outstanding  what the employee still owes on other running loans (0 if none)
     */
    public List<LoanEligibilityCheck> check(BigDecimal amount, Short tenure,
                                            Optional<BigDecimal> netSalary, BigDecimal outstanding) {
        return List.of(
                salaryCheck(amount, netSalary),
                outstandingCheck(outstanding),
                tenureCheck(tenure));
    }

    private LoanEligibilityCheck salaryCheck(BigDecimal amount, Optional<BigDecimal> netSalary) {
        if (netSalary.isEmpty()) {
            return new LoanEligibilityCheck("Net salary not available, so the 50% limit cannot be verified", false);
        }
        BigDecimal max = calculator.maxEligibleAmount(netSalary.get());
        boolean within = amount != null && amount.compareTo(max) <= 0;
        return new LoanEligibilityCheck(
                (within ? "Amount within 50% net salary limit" : "Amount exceeds 50% net salary limit")
                        + " (max " + rupees(max) + ")",
                within);
    }

    private LoanEligibilityCheck outstandingCheck(BigDecimal outstanding) {
        boolean none = outstanding == null || outstanding.signum() <= 0;
        return new LoanEligibilityCheck(
                none ? "No active loan outstanding" : "Active loan outstanding of " + rupees(outstanding),
                none);
    }

    private LoanEligibilityCheck tenureCheck(Short tenure) {
        boolean within = tenure != null && tenure >= 1 && tenure <= LoanEmiCalculator.MAX_TENURE_MONTHS;
        return new LoanEligibilityCheck(
                (within ? "Tenure within policy" : "Tenure exceeds policy")
                        + " (max " + LoanEmiCalculator.MAX_TENURE_MONTHS + " months)",
                within);
    }

    /** 42500 -> "₹42,500" (Indian digit grouping). */
    private static String rupees(BigDecimal value) {
        NumberFormat format = NumberFormat.getNumberInstance(new Locale("en", "IN"));
        format.setMaximumFractionDigits(2);
        return "₹" + format.format(value);
    }
}