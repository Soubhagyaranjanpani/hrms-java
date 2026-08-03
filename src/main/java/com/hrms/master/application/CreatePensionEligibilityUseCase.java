package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.PensionEligibility;
import com.hrms.master.dto.CreatePensionEligibilityRequest;
import com.hrms.master.infrastructure.PensionEligibilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePensionEligibilityUseCase {

    private final PensionEligibilityRepository repository;

    public ApiResponse<DefaultResponse> execute(CreatePensionEligibilityRequest request) {

        if (repository.existsByName(request.getName())) {
            throw new RuntimeException("Pension Eligibility already exists");
        }

        PensionEligibility entity = new PensionEligibility();
        entity.setName(request.getName());

        repository.save(entity);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Pension Eligibility Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}