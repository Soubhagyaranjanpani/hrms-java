package com.hrms.employee.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employee_skill")
@Data
public class EmployeeSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    private String skillName;
    private Integer proficiency;
}
