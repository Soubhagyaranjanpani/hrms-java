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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private String skillName;

    private Integer proficiency;

    private Integer flag = 0;
}