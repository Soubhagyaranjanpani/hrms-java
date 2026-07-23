package com.hrms.employment_type.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employment_type.domain.EmploymentType;          // Fixed import
import com.hrms.employment_type.infrastructure.EmploymentTypeRepository; // Fixed import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeEmploymentTypeStatusUseCase {

    private final EmploymentTypeRepository employmentTypeRepository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        EmploymentType type = employmentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employment Type not found"));

        type.setIsActive(!type.getIsActive());

        employmentTypeRepository.save(type);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Employment Type Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}