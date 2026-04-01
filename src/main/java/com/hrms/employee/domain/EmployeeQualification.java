package com.hrms.employee.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employee_qualification")
@Data
public class EmployeeQualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    private String degree;
    private String institution;
    private Integer year;
}
