package com.hrms.employee.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "employee_service_history")
@Data
public class EmployeeServiceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    private String designation;
    private String department;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String orderReference;
}
