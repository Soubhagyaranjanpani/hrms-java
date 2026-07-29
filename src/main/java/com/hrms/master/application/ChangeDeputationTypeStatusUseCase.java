package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.DeputationType;
import com.hrms.master.infrastructure.DeputationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeDeputationTypeStatusUseCase {

    private final DeputationTypeRepository deputationTypeRepository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        DeputationType deputationType = deputationTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deputation Type not found"));

        deputationType.setIsActive(!deputationType.getIsActive());

        deputationTypeRepository.save(deputationType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Deputation Type Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}