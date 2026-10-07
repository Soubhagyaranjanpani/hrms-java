package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.ActionType;
import com.hrms.master.dto.ActionTypeCreateReq;
import com.hrms.master.infrastructure.ActionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateActionTypeUseCase {

    private final ActionTypeRepository actionTypeRepository;

    public ApiResponse<DefaultResponse> execute(ActionTypeCreateReq request) {

        if (actionTypeRepository.existsByName(request.getName())) {
            throw new RuntimeException("Action Type already exists");
        }

        ActionType actionType = new ActionType();
        actionType.setName(request.getName());

        actionTypeRepository.save(actionType);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Action Type Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}