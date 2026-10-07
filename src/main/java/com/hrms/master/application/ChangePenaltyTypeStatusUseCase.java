package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.PenaltyType;
import com.hrms.master.infrastructure.PenaltyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePenaltyTypeStatusUseCase {

    private final PenaltyTypeRepository penaltyTypeRepository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        PenaltyType penaltyType = penaltyTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Penalty Type not found"));

        penaltyType.setIsActive(!penaltyType.getIsActive());

        penaltyTypeRepository.save(penaltyType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Penalty Type Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}