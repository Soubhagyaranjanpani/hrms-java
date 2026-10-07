package com.hrms.leave.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.leave.infrastructure.LeavePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivateLeavePolicyUseCase {

    private final LeavePolicyRepository leavePolicyRepo;

    public ApiResponse<String> execute(Long id) {

        leavePolicyRepo.deactivateAll();

        leavePolicyRepo.activateById(id);

        return ResponseUtils.createSuccessResponse("Policy activated",null);
    }
}
