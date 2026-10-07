package com.hrms.payroll.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class ProcessPayrollRequest {
    private String     yearMonth;
    private LocalDate  paymentDate;
    private List<Long> recordIds;   // null = process all PENDING for month
}