package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.AwardType;
import com.hrms.master.dto.AwardTypeUpdateReq;
import com.hrms.master.infrastructure.AwardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAwardTypeUseCase {

    private final AwardTypeRepository awardTypeRepository;

    public ApiResponse<DefaultResponse> execute(AwardTypeUpdateReq request) {

        AwardType awardType = awardTypeRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Award Type not found"));

        awardType.setName(request.getName());

        awardTypeRepository.save(awardType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Award Type Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}