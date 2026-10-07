package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.DeputationType;
import com.hrms.master.dto.DeputationTypeUpdateReq;
import com.hrms.master.infrastructure.DeputationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateDeputationTypeUseCase {

    private final DeputationTypeRepository deputationTypeRepository;

    public ApiResponse<DefaultResponse> execute(DeputationTypeUpdateReq request) {

        DeputationType deputationType = deputationTypeRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Deputation Type not found"));

        deputationType.setName(request.getName());

        deputationTypeRepository.save(deputationType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Deputation Type Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}