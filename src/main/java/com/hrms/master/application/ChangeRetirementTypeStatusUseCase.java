package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.RetirementType;
import com.hrms.master.infrastructure.RetirementTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeRetirementTypeStatusUseCase {

    private final RetirementTypeRepository repository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        RetirementType type = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Retirement Type not found"));

        type.setIsActive(!type.getIsActive());

        repository.save(type);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Retirement Type Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}