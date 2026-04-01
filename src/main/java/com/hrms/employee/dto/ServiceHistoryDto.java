package com.hrms.employee.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ServiceHistoryDto {

    private String designation;
    private String department;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String orderReference;
}
