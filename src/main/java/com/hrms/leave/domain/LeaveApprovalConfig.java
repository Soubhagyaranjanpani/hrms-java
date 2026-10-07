package com.hrms.leave.domain;

import com.hrms.leave.domain.enums.ApprovalRoleType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "leave_approval_config")
@Data
public class LeaveApprovalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LeaveType leaveType;

    private Integer level;

    @Enumerated(EnumType.STRING)
    private ApprovalRoleType roleType;

    private Boolean isActive = true;
}
