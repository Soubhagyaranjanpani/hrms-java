package com.hrms.employee.domain;

import com.hrms.master.domain.Designation;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "employee_designation")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"employee", "designation"})
public class EmployeeDesignation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, updatable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designation;

    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    // Consider replacing with Spring Data JPA auditing (@CreatedDate/@LastModifiedDate
    // + @EntityListeners(AuditingEntityListener.class)) instead of manual timestamps
    // set in the service layer.
    @Column
    private LocalDate updatedDate;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    // Optimistic locking: without this, concurrent changeStatus()/update() calls
    // on the same row can silently overwrite each other instead of failing loudly.
    @Version
    private Long version;
}