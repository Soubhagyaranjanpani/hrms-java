package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.RetirementType;
import com.hrms.master.dto.CreateRetirementTypeRequest;
import com.hrms.master.infrastructure.RetirementTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateRetirementTypeUseCase {

    private final RetirementTypeRepository repository;

    public ApiResponse<DefaultResponse> execute(CreateRetirementTypeRequest request) {

        if (repository.existsByName(request.getName())) {
            throw new RuntimeException("Retirement Type already exists");
        }

        RetirementType type = new RetirementType();
        type.setName(request.getName());

        repository.save(type);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Retirement Type Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}