package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.ActionType;
import com.hrms.master.dto.ActionTypeUpdateReq;
import com.hrms.master.infrastructure.ActionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateActionTypeUseCase {

    private final ActionTypeRepository actionTypeRepository;

    public ApiResponse<DefaultResponse> execute(ActionTypeUpdateReq request) {

        ActionType actionType = actionTypeRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Action Type not found"));

        actionType.setName(request.getName());

        actionTypeRepository.save(actionType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Action Type Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}