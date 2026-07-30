package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.PenaltyType;
import com.hrms.master.dto.PenaltyTypeUpdateReq;
import com.hrms.master.infrastructure.PenaltyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatePenaltyTypeUseCase {

    private final PenaltyTypeRepository penaltyTypeRepository;

    public ApiResponse<DefaultResponse> execute(PenaltyTypeUpdateReq request) {

        PenaltyType penaltyType = penaltyTypeRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Penalty Type not found"));

        penaltyType.setName(request.getName());

        penaltyTypeRepository.save(penaltyType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Penalty Type Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}