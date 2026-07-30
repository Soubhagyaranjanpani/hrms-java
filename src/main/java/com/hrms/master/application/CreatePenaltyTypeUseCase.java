package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.PenaltyType;
import com.hrms.master.dto.PenaltyTypeCreateReq;
import com.hrms.master.infrastructure.PenaltyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePenaltyTypeUseCase {

    private final PenaltyTypeRepository penaltyTypeRepository;

    public ApiResponse<DefaultResponse> execute(PenaltyTypeCreateReq request) {

        if (penaltyTypeRepository.existsByName(request.getName())) {
            throw new RuntimeException("Penalty Type already exists");
        }

        PenaltyType penaltyType = new PenaltyType();
        penaltyType.setName(request.getName());

        penaltyTypeRepository.save(penaltyType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Penalty Type Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}