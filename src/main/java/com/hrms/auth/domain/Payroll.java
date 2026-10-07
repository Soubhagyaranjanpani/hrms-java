package com.hrms.auth.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    private Double basicSalary;
    private Double allowances;
    private Double deductions;

    private Double netSalary;

    private String month;
}
