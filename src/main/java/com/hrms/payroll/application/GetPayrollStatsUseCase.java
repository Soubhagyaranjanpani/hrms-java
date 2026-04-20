package com.hrms.payroll.application;



import com.hrms.payroll.dto.PayrollStatsResponse;
import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPayrollStatsUseCase {

    private final PayrollRepository repo;

    public PayrollStatsResponse execute(String yearMonth) {
        Object[] agg = repo.aggregateForMonth(yearMonth);

        PayrollStatsResponse s = new PayrollStatsResponse();

        if (agg == null || agg[0] == null) {
            // no data — return zeroes
            s.setTotalPayroll(0.0);
            s.setTotalBasic(0.0); s.setTotalAllowances(0.0);
            s.setTotalDeductions(0.0); s.setTotalGross(0.0);
            s.setTotalCount(0); s.setProcessedCount(0); s.setPendingCount(0);
            s.setAvgSalary(0.0);
            s.setBasicPercent(0.0); s.setAllowancesPercent(0.0); s.setDeductionsPercent(0.0);
            return s;
        }

        double net          = toDouble(agg[0]);
        double basic        = toDouble(agg[1]);
        double allowances   = toDouble(agg[2]);
        double deductions   = toDouble(agg[3]);
        long   total        = toLong(agg[4]);
        long   processed    = toLong(agg[5]);
        double gross        = basic + allowances;

        s.setTotalPayroll(net);
        s.setTotalBasic(basic);
        s.setTotalAllowances(allowances);
        s.setTotalDeductions(deductions);
        s.setTotalGross(gross);
        s.setTotalCount((int) total);
        s.setProcessedCount((int) processed);
        s.setPendingCount((int)(total - processed));
        s.setAvgSalary(total > 0 ? net / total : 0);
        s.setBasicPercent(gross > 0 ? (basic / gross) * 100 : 0);
        s.setAllowancesPercent(gross > 0 ? (allowances / gross) * 100 : 0);
        s.setDeductionsPercent(gross > 0 ? (deductions / gross) * 100 : 0);

        return s;
    }

    private double toDouble(Object o) { return o == null ? 0 : ((Number) o).doubleValue(); }
    private long   toLong(Object o)   { return o == null ? 0 : ((Number) o).longValue(); }
}
