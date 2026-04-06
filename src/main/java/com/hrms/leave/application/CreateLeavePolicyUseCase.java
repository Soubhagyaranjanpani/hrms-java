package com.hrms.leave.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.leave.domain.LeavePolicy;
import com.hrms.leave.domain.LeaveType;
import com.hrms.leave.dto.LeavePolicyRequest;
import com.hrms.leave.infrastructure.LeavePolicyRepository;
import com.hrms.leave.infrastructure.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateLeavePolicyUseCase {

    private final LeavePolicyRepository policyRepo;
    private final LeaveTypeRepository leaveTypeRepo;

    public ApiResponse<String> execute(LeavePolicyRequest request) {

        LeaveType leaveType = leaveTypeRepo.findById(request.getLeaveTypeId()).orElse(null);

        if (leaveType == null) {
            return ResponseUtils.createFailureResponse(null,null,"Leave type not found",404);
        }

        // 🔥 Only one active policy per leave type
        if (Boolean.TRUE.equals(request.getIsActive())) {
            policyRepo.deactivateByLeaveType(leaveType.getId());
        }

        LeavePolicy policy = new LeavePolicy();

        policy.setLeaveType(leaveType);
        policy.setCarryForwardAllowed(request.getCarryForwardAllowed());
        policy.setMaxCarryForwardDays(request.getMaxCarryForwardDays());
        policy.setExpiryType(request.getExpiryType());

        policy.setAccrualEnabled(request.getAccrualEnabled());
        policy.setAccrualPerMonth(request.getAccrualPerMonth());

        policy.setRequiresApproval(request.getRequiresApproval());
        policy.setMaxApprovalLevels(request.getMaxApprovalLevels());

        policy.setSandwichPolicyEnabled(request.getSandwichPolicyEnabled());
        policy.setHolidayIncludedInLeave(request.getHolidayIncludedInLeave());
        policy.setWeekendIncludedInLeave(request.getWeekendIncludedInLeave());

        policy.setAllowHalfDay(request.getAllowHalfDay());
        policy.setAllowBackdatedLeave(request.getAllowBackdatedLeave());

        policy.setMaxLeaveDaysPerRequest(request.getMaxLeaveDaysPerRequest());
        policy.setDocumentRequired(request.getDocumentRequired());

        policy.setIsActive(request.getIsActive());

        policyRepo.save(policy);

        return ResponseUtils.createSuccessResponse("Leave policy created",null);
    }
}
