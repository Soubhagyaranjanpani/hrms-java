package com.hrms.serviceBook.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "search_book")
@Data
public class SearchBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeName;

    private String department;

    private String designation;

    private String status;

    private LocalDate joiningDate;

    private LocalDate retirementDate;

    private Integer flag = 0;
}