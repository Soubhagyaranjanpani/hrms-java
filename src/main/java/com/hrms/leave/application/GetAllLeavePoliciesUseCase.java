package com.hrms.leave.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.leave.domain.LeavePolicy;
import com.hrms.leave.infrastructure.LeavePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllLeavePoliciesUseCase {

    private final LeavePolicyRepository leavePolicyRepository;

    public ApiResponse<List<LeavePolicy>> execute() {

        List<LeavePolicy> policies = leavePolicyRepository.findByIsActiveTrue();

        return ResponseUtils.createSuccessResponse(
                policies,
                new TypeReference<>() {}
        );
    }
}
