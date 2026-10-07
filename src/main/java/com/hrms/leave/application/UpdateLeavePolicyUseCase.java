package com.hrms.leave.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.leave.domain.LeavePolicy;
import com.hrms.leave.dto.LeavePolicyRequest;
import com.hrms.leave.infrastructure.LeavePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateLeavePolicyUseCase {

    private final LeavePolicyRepository policyRepo;

    public ApiResponse<String> execute(Long id, LeavePolicyRequest request) {

        LeavePolicy policy = policyRepo.findById(id).orElse(null);

        if (policy == null) {
            return ResponseUtils.createFailureResponse(null,null,"Policy not found",404);
        }

        if (Boolean.TRUE.equals(request.getIsActive())) {
            policyRepo.deactivateByLeaveType(policy.getLeaveType().getId());
        }

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

        return ResponseUtils.createSuccessResponse("Leave policy updated",null);
    }
}
