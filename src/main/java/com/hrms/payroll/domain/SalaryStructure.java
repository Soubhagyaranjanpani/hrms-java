package com.hrms.payroll.domain;



import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Stores the recurring salary structure for each employee.
 * Used by BulkGenerate to auto-fill payroll records.
 */
@Entity
@Getter
@Setter
@Table(name = "salary_structures")
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    // CTC breakdown
    private Double basicSalary    = 0.0;
    private Double hra            = 0.0;
    private Double travelAllow    = 0.0;
    private Double medicalAllow   = 0.0;
    private Double specialAllow   = 0.0;

    // Deduction config
    private Double providentFund  = 0.0;  // fixed amount or 12% rule applied in code
    private Double professionalTax= 0.0;
    private Double incomeTax      = 0.0;

    private Double ctc            = 0.0;  // annual CTC

    private Boolean isActive = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist  public void prePersist()  { this.createdAt = LocalDateTime.now(); }
    @PreUpdate   public void preUpdate()   { this.updatedAt = LocalDateTime.now(); }
}
