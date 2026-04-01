package com.hrms.leave.domain;

import com.hrms.employee.domain.Employee;
import com.hrms.leave.domain.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_approval_history")
@Data
public class LeaveApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Leave leave;

    @ManyToOne
    private Employee approver;

    private Integer level; // 1 = Manager, 2 = HR

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    private String remarks;

    private LocalDateTime actionAt;
}
