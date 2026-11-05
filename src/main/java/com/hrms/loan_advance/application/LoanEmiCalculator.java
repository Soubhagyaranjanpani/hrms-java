package com.hrms.loan_advance.application;

import com.hrms.loan_advance.domain.LoanType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Flat-rate EMI = (principal + principal * annualRate% * years) / months.
 * Drives the form's "Live EMI Preview". Rates below are PLACEHOLDERS: set your policy here.
 */
@Component
public class LoanEmiCalculator {

    private static final Map<LoanType, BigDecimal> INTEREST_RATES = Map.of(
            LoanType.SALARY_ADVANCE, BigDecimal.ZERO,
            LoanType.PERSONAL_LOAN, new BigDecimal("12"),
            LoanType.EMERGENCY_LOAN, BigDecimal.ZERO
    );

    private static final BigDecimal MAX_ELIGIBLE_RATIO = new BigDecimal("0.5"); // 50% of net salary

    /** Policy: tenure of a loan is at most this many months. */
    public static final int MAX_TENURE_MONTHS = 12;

    public BigDecimal interestRateFor(LoanType loanType) {
        return loanType == null ? BigDecimal.ZERO : INTEREST_RATES.getOrDefault(loanType, BigDecimal.ZERO);
    }

    /** null when the net salary is unknown. */
    public BigDecimal maxEligibleAmount(BigDecimal netMonthlySalary) {
        if (netMonthlySalary == null) return null;
        return netMonthlySalary.multiply(MAX_ELIGIBLE_RATIO).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateEmi(BigDecimal principal, Short tenureMonths, LoanType loanType) {
        if (principal == null || tenureMonths == null || tenureMonths <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        BigDecimal years = BigDecimal.valueOf(tenureMonths)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal interest = principal
                .multiply(interestRateFor(loanType).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                .multiply(years);
        return principal.add(interest)
                .divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
    }

    /** Principal + flat interest for the whole tenure. */
    public BigDecimal totalPayable(BigDecimal principal, Short tenureMonths, LoanType loanType) {
        if (principal == null || tenureMonths == null || tenureMonths <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        BigDecimal years = BigDecimal.valueOf(tenureMonths)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal interest = principal
                .multiply(interestRateFor(loanType).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                .multiply(years);
        return principal.add(interest).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * The amount of every EMI. All are the rounded EMI except the last, which takes the rounding
     * difference so the installments add up exactly to totalPayable.
     */
    public List<BigDecimal> installments(BigDecimal principal, Short tenureMonths, LoanType loanType) {
        List<BigDecimal> amounts = new ArrayList<>();
        if (principal == null || tenureMonths == null || tenureMonths <= 0) return amounts;

        BigDecimal emi = calculateEmi(principal, tenureMonths, loanType);
        BigDecimal total = totalPayable(principal, tenureMonths, loanType);
        for (int i = 1; i < tenureMonths; i++) {
            amounts.add(emi);
        }
        amounts.add(total.subtract(emi.multiply(BigDecimal.valueOf(tenureMonths - 1L))));
        return amounts;
    }
}