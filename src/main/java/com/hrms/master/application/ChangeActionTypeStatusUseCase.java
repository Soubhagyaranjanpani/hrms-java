package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.ActionType;
import com.hrms.master.infrastructure.ActionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeActionTypeStatusUseCase {

    private final ActionTypeRepository actionTypeRepository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        ActionType actionType = actionTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action Type not found"));

        actionType.setIsActive(!actionType.getIsActive());

        actionTypeRepository.save(actionType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Action Type Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}