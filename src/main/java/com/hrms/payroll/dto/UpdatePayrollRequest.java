package com.hrms.payroll.dto;



import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdatePayrollRequest {
    private Double basicSalary;
    private Double hra;
    private Double travelAllow;
    private Double medicalAllow;
    private Double specialAllow;
    private Double otherEarnings;
    private Double providentFund;
    private Double professionalTax;
    private Double incomeTax;
    private Double loanDeduction;
    private Double otherDeductions;
    private Integer workingDays;
    private Integer paidDays;
    private Integer lopDays;
    private String remarks;
}

