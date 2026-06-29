package com.hrms.serviceBook.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchBookResponse {

    private Long id;

    private String employeeName;

    private String department;

    private String designation;

    private String status;

    private LocalDate joiningDate;

    private LocalDate retirementDate;
}