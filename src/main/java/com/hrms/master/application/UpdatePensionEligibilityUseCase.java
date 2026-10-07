package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.PensionEligibility;
import com.hrms.master.dto.UpdatePensionEligibilityRequest;
import com.hrms.master.infrastructure.PensionEligibilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatePensionEligibilityUseCase {

    private final PensionEligibilityRepository repository;

    public ApiResponse<DefaultResponse> execute(UpdatePensionEligibilityRequest request) {

        PensionEligibility entity = repository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Pension Eligibility not found"));

        entity.setName(request.getName());

        repository.save(entity);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Pension Eligibility Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}