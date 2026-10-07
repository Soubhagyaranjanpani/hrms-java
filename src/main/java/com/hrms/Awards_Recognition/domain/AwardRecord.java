package com.hrms.Awards_Recognition.domain;


import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.master.domain.AwardType;
import jakarta.persistence.*;
        import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "award_records")
public class AwardRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "award_name", length = 200)
    private String awardName;

    @Column(name = "award_date")
    private LocalDate awardDate;

    // ✅ Dropdown 1 - Award Type Master
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "award_type_id")
    private AwardType awardType;

    // ✅ Dropdown 2 - Issued By (EmployeeDesignation)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by_id")
    private EmployeeDesignation issuedBy;

    @Column(name = "description", length = 1000)
    private String description;

    // Snapshot fields
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

    private String processedBy;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}