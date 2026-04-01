package com.hrms.leave.domain;

import com.hrms.leave.domain.enums.LeaveExpiryType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "leave_policy")
@Data
public class LeavePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LeaveType leaveType;

    // Basic rules
    private Boolean carryForwardAllowed;
    private Integer maxCarryForwardDays;

    @Enumerated(EnumType.STRING)
    private LeaveExpiryType expiryType;

    // Accrual
    private Boolean accrualEnabled;
    private Integer accrualPerMonth;

    // Approval
    private Boolean requiresApproval;
    private Integer maxApprovalLevels;

    // Advanced Config Flags (🔥 IMPORTANT)
    private Boolean sandwichPolicyEnabled;
    private Boolean holidayIncludedInLeave;
    private Boolean weekendIncludedInLeave;

    private Boolean allowHalfDay;
    private Boolean allowBackdatedLeave;

    private Integer maxLeaveDaysPerRequest;

    private Boolean documentRequired; // e.g. medical certificate

    private Boolean isActive = true;
}
