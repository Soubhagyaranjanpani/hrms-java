package com.hrms.employee.domain;

import com.hrms.master.domain.Department;
import com.hrms.master.domain.Designation;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "employee_service_history")
@Data
public class EmployeeServiceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "designation_id")
    private Designation designation;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // 🔥 Snapshot (VERY IMPORTANT)
    private String departmentNameSnapshot;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String orderReference;
}
