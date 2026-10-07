package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.AwardType;
import com.hrms.master.infrastructure.AwardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeAwardTypeStatusUseCase {

    private final AwardTypeRepository awardTypeRepository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        AwardType awardType = awardTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Award Type not found"));

        awardType.setIsActive(!awardType.getIsActive());

        awardTypeRepository.save(awardType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Award Type Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}