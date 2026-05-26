package com.hrms.payroll.dto;



import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SalaryStructureResponse {
    private Long   id;
    private Long   employeeId;
    private String employeeName;    // firstName + lastName
    private String employeeCode;
    private String department;
    private String branch;

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
