package com.hrms.training.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "training_records")
public class TrainingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "training_name", nullable = false, length = 255)
    private String trainingName;

    @Column(name = "provider", length = 255)
    private String provider;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private Integer hours;

    // "Yes" / "No" / "Pending" — simple dropdown, no dedicated master table
    @Column(name = "certification", length = 20)
    private String certification;

    // Snapshot of department/designation at the time of training — the form shows
    // these as read-only "Auto" fields.
    @Column(name = "department_name", length = 150)
    private String departmentName;

    @Column(name = "designation_name", length = 150)
    private String designationName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "document_path", length = 500)
    private String documentPath;

    @Column(name = "document_name", length = 255)
    private String documentName;

    private Boolean isDeleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

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
