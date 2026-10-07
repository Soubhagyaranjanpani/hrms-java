package com.hrms.disciplinary.domain;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.master.domain.ActionType;
import com.hrms.master.domain.PenaltyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "disciplinary_records")
public class DisciplinaryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "case_number", unique = true, length = 100)
    private String caseNumber;

    @Column(name = "incident_date")
    private LocalDate incidentDate;

    // ✅ Dropdown 1 - Action Type Master
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_type_id")
    private ActionType actionType;

    // ✅ Dropdown 2 - Investigation Officer (EmployeeDesignation)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investigation_officer_id")
    private EmployeeDesignation investigationOfficer;

    // ✅ Dropdown 3 - Penalty Type Master
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "penalty_type_id")
    private PenaltyType penaltyType;

    @Column(name = "resolution_date")
    private LocalDate resolutionDate;

    @Column(name = "remarks", length = 1000)
    private String remarks;

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