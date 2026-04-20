package com.hrms.payroll.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class PayrollRecordResponse {
    private Long   id;
    private Long   employeeId;
    private String employee;
    private String employeeCode;
    private String department;
    private String branch;
    private String designation;
    private String yearMonth;
    private String payrollMonth;

    // Earnings
    private Double basicSalary;
    private Double hra;
    private Double travelAllow;
    private Double medicalAllow;
    private Double specialAllow;
    private Double otherEarnings;
    private Double grossEarnings;

    // Deductions
    private Double providentFund;
    private Double professionalTax;
    private Double incomeTax;
    private Double loanDeduction;
    private Double otherDeductions;
    private Double totalDeductions;

    // Net
    private Double netSalary;

    // Days
    private Integer workingDays;
    private Integer paidDays;
    private Integer lopDays;

    // Status + meta
    private String    status;
    private LocalDate paymentDate;
    private String    remarks;
    private String    aiInsight;
    private String    processedBy;
}