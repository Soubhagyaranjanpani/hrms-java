package com.hrms.leave.domain;

import com.hrms.employee.domain.Employee;
import com.hrms.leave.domain.enums.LeaveStatus;
import com.hrms.leave.domain.enums.HalfDaySession;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_request")
@Data
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private LeaveType leaveType;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isHalfDay = false;

    @Enumerated(EnumType.STRING)
    private HalfDaySession halfDaySession;
    // MORNING / AFTERNOON

    private Double totalDays;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    private String reason;

    private String rejectionReason;


    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_approver_id")
    private Employee currentApprover;

    private Integer currentApprovalLevel = 1;
}
