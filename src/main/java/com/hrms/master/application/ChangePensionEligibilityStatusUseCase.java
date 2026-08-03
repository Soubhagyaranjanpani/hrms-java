package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.PensionEligibility;
import com.hrms.master.infrastructure.PensionEligibilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePensionEligibilityStatusUseCase {

    private final PensionEligibilityRepository repository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        PensionEligibility entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pension Eligibility not found"));

        entity.setIsActive(!entity.getIsActive());

        repository.save(entity);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Pension Eligibility Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}