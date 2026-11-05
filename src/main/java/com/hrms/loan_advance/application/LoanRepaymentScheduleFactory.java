package com.hrms.loan_advance.application;

import com.hrms.loan_advance.domain.LoanAdvance;
import com.hrms.loan_advance.domain.LoanRepayment;
import com.hrms.loan_advance.domain.LoanStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates the EMI rows of an approved loan. The first EMI is due in the month AFTER disbursement,
 * then one per month. Pure logic: it does not save anything.
 */
@Component
@RequiredArgsConstructor
public class LoanRepaymentScheduleFactory {

    private final LoanEmiCalculator calculator;

    public List<LoanRepayment> build(LoanAdvance loan) {
        List<BigDecimal> amounts = calculator.installments(loan.getAmount(), loan.getTenure(), loan.getLoanType());
        YearMonth firstDue = YearMonth.from(loan.getDisbursedOn()).plusMonths(1);

        List<LoanRepayment> schedule = new ArrayList<>();
        for (int i = 0; i < amounts.size(); i++) {
            LoanRepayment row = new LoanRepayment();
            row.setLoanId(loan.getLoanId());
            row.setEmiNo(i + 1);
            row.setDueMonth(firstDue.plusMonths(i).toString());   // "2026-12"
            row.setAmount(amounts.get(i));
            row.setStatus(LoanStatus.UPCOMING);
            schedule.add(row);
        }
        return schedule;
    }
}