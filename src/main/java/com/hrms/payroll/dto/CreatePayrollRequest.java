package com.hrms.payroll.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePayrollRequest {
    private Long   employeeId;
    private String yearMonth;       // "YYYY-MM"
    private Double basicSalary;
    private Double allowances;
    private Double deductions;
}
