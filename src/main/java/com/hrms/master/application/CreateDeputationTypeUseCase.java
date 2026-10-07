package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.DeputationType;
import com.hrms.master.dto.DeputationTypeCreateReq;
import com.hrms.master.infrastructure.DeputationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateDeputationTypeUseCase {

    private final DeputationTypeRepository deputationTypeRepository;

    public ApiResponse<DefaultResponse> execute(DeputationTypeCreateReq request) {

        if (deputationTypeRepository.existsByName(request.getName())) {
            throw new RuntimeException("Deputation Type already exists");
        }

        DeputationType deputationType = new DeputationType();
        deputationType.setName(request.getName());

        deputationTypeRepository.save(deputationType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Deputation Type Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}