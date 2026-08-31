package com.hrms.employee.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employee_nominee")
@Data
public class EmployeeNominee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "serial_no")
    private Integer serialNo;

    @Column(name = "nominee_name", length = 100)
    private String name;

    @Column(name = "relation", length = 30)
    private String relation;

    @Column(name = "phone", length = 15)
    private String phone;
}