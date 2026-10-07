package com.hrms.task.domain;


import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "performance_reviews")
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private Double rating;           // e.g. 4.8  (out of 5.0)

    private Integer totalGoals;
    private Integer achievedGoals;

    private String improvementPercent; // e.g. "+12%"

    // Outstanding, Excellent, Great, Good, Satisfactory
    private String status;

    private String reviewCycle;      // e.g. "Q1 2026"

    private Boolean isDeleted = false;

    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}
