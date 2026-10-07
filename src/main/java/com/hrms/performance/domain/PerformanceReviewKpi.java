package com.hrms.performance.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "performance_review_kpi")
public class PerformanceReviewKpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    @ManyToOne
    private Employee reviewer;

    private String type;

    private Double rating;

    @Column(length = 1000)
    private String comments;
}
