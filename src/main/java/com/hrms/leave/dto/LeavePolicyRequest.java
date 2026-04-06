package com.hrms.leave.dto;

import com.hrms.leave.domain.enums.LeaveExpiryType;
import lombok.Data;

@Data
public class LeavePolicyRequest {

    private Long leaveTypeId;

    private Boolean carryForwardAllowed;
    private Integer maxCarryForwardDays;

    private LeaveExpiryType expiryType;

    private Boolean accrualEnabled;
    private Integer accrualPerMonth;

    private Boolean requiresApproval;
    private Integer maxApprovalLevels;

    private Boolean sandwichPolicyEnabled;
    private Boolean holidayIncludedInLeave;
    private Boolean weekendIncludedInLeave;

    private Boolean allowHalfDay;
    private Boolean allowBackdatedLeave;

    private Integer maxLeaveDaysPerRequest;

    private Boolean documentRequired;

    private Boolean isActive;
}
