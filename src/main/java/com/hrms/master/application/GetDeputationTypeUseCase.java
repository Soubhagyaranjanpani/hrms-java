package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.DeputationType;
import com.hrms.master.dto.DeputationTypeResponse;
import com.hrms.master.infrastructure.DeputationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetDeputationTypeUseCase {

    private final DeputationTypeRepository deputationTypeRepository;

    public ApiResponse<List<DeputationTypeResponse>> execute(Integer flag) {

        List<DeputationType> list;

        if (flag == 1) {
            list = deputationTypeRepository.findByIsActiveTrue();
        } else {
            list = deputationTypeRepository.findAll();
        }

        List<DeputationTypeResponse> response = list.stream().map(d -> {
            DeputationTypeResponse res = new DeputationTypeResponse();
            res.setId(d.getId());
            res.setName(d.getName());
            res.setIsActive(d.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}