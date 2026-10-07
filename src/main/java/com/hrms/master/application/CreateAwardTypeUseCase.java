package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.AwardType;
import com.hrms.master.dto.AwardTypeCreateReq;
import com.hrms.master.infrastructure.AwardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateAwardTypeUseCase {

    private final AwardTypeRepository awardTypeRepository;

    public ApiResponse<DefaultResponse> execute(AwardTypeCreateReq request) {

        if (awardTypeRepository.existsByName(request.getName())) {
            throw new RuntimeException("Award Type already exists");
        }

        AwardType awardType = new AwardType();
        awardType.setName(request.getName());

        awardTypeRepository.save(awardType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Award Type Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}