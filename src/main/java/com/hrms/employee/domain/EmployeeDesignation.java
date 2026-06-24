package com.hrms.employee.domain;

import com.hrms.master.domain.Designation;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "employee_designation")
@Data
public class EmployeeDesignation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "designation_id")
    private Designation designation;

    private LocalDate createdDate;

    // ✅ NEW FIELDS (add these)
    private Boolean isActive = true;   // default: active
    private Boolean isDeleted = false; // default: not deleted
}