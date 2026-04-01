package com.hrms.leave.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(
        name = "leave_balance",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"employee_id", "leave_type_id", "year"})
        }
)
@Data
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private LeaveType leaveType;

    private Integer year;

    private Double totalAllocated;
    private Double used;
    private Double remaining;

    private Double carriedForward;

    private LocalDate expiryDate; // calculated based on policy
}
