package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.ActionType;
import com.hrms.master.dto.ActionTypeResponse;
import com.hrms.master.infrastructure.ActionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetActionTypeUseCase {

    private final ActionTypeRepository actionTypeRepository;

    public ApiResponse<List<ActionTypeResponse>> execute(Integer flag) {

        List<ActionType> list;

        if (flag == 1) {
            list = actionTypeRepository.findByIsActiveTrue();
        } else {
            list = actionTypeRepository.findAll();
        }

        List<ActionTypeResponse> response = list.stream().map(a -> {
            ActionTypeResponse res = new ActionTypeResponse();
            res.setId(a.getId());
            res.setName(a.getName());
            res.setIsActive(a.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}