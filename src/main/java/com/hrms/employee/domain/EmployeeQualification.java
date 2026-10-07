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
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // ── legacy fields (kept for backward compatibility) ──
    private String degree;
    private String institution;
    private Integer year;

    // ── new fields to match current UI ──
    @Column(name = "level", length = 30)
    private String level; // 10th, 12th, graduation, post_graduation, diploma, phd, other

    @Column(name = "board", length = 100)
    private String board;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "university", length = 150)
    private String university;

    @Column(name = "cgpa")
    private Double cgpa;
}