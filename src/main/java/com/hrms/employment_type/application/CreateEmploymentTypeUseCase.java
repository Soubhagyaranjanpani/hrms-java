package com.hrms.employment_type.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employment_type.domain.EmploymentType;                 // fixed
import com.hrms.employment_type.dto.EmploymentTypeCreateReq;          // fixed
import com.hrms.employment_type.infrastructure.EmploymentTypeRepository; // fixed
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateEmploymentTypeUseCase {

    private final EmploymentTypeRepository employmentTypeRepository;

    public ApiResponse<DefaultResponse> execute(EmploymentTypeCreateReq request) {

        if (employmentTypeRepository.existsByName(request.getName())) {
            throw new RuntimeException("Employment Type already exists");
        }

        EmploymentType type = new EmploymentType();
        type.setName(request.getName());

        employmentTypeRepository.save(type);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Employment Type Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}