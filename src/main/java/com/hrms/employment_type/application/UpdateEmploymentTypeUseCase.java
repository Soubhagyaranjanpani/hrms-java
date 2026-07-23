package com.hrms.employment_type.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employment_type.domain.EmploymentType;                 // fixed
import com.hrms.employment_type.dto.EmploymentTypeUpdateReq;          // fixed
import com.hrms.employment_type.infrastructure.EmploymentTypeRepository; // fixed
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateEmploymentTypeUseCase {

    private final EmploymentTypeRepository employmentTypeRepository;

    public ApiResponse<DefaultResponse> execute(EmploymentTypeUpdateReq request) {

        EmploymentType type = employmentTypeRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Employment Type not found"));

        type.setName(request.getName());

        employmentTypeRepository.save(type);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Employment Type Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}