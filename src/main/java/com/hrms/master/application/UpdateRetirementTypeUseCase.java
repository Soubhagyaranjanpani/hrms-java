package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.RetirementType;
import com.hrms.master.dto.UpdateRetirementTypeRequest;
import com.hrms.master.infrastructure.RetirementTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateRetirementTypeUseCase {

    private final RetirementTypeRepository repository;

    public ApiResponse<DefaultResponse> execute(UpdateRetirementTypeRequest request) {

        RetirementType type = repository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Retirement Type not found"));

        type.setName(request.getName());

        repository.save(type);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Retirement Type Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}