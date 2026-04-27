package com.hrms.employee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "employee_grades")
public class EmployeeGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;                // "GRADE-A", "LEVEL-10", "L1"

    @Column(nullable = false, length = 100)
    private String name;                // "Grade A", "Level 10"

    @Column(length = 500)
    private String description;         // "Senior Management Level"

    private Double minSalary;           // Minimum salary range
    private Double maxSalary;           // Maximum salary range

    private Double gradePay;            // Grade pay amount (for government)

    private Integer level;              // Numeric level for sorting (1,2,3...)

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}