package com.hrms.payroll.dto;



import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SalaryStructureRequest {
    private Long   employeeId;
    private Double basicSalary;
    private Double hra;
    private Double travelAllow;
    private Double medicalAllow;
    private Double specialAllow;
    private Double providentFund;
    private Double professionalTax;
    private Double incomeTax;
    private Double ctc;

}
