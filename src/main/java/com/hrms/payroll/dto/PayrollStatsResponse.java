package com.hrms.payroll.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayrollStatsResponse {
    private Double totalPayroll;      // sum of netSalary
    private Integer processedCount;
    private Integer totalCount;
    private Integer pendingCount;
    private Double  avgSalary;
    private Double  totalBasic;
    private Double  totalAllowances;
    private Double  totalDeductions;
    private Double  totalGross;       // basic + allowances
    private Double  basicPercent;
    private Double  allowancesPercent;
    private Double  deductionsPercent;
}
