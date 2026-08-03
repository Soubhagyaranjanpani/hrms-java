package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.RetirementType;
import com.hrms.master.dto.RetirementTypeResponse;
import com.hrms.master.infrastructure.RetirementTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetRetirementTypeUseCase {

    private final RetirementTypeRepository repository;

    public ApiResponse<List<RetirementTypeResponse>> execute(Integer flag) {

        List<RetirementType> list;

        if (flag == 1) {
            list = repository.findByIsActiveTrue();
        } else {
            list = repository.findAll();
        }

        List<RetirementTypeResponse> response = list.stream().map(r -> {
            RetirementTypeResponse res = new RetirementTypeResponse();
            res.setId(r.getId());
            res.setName(r.getName());
            res.setIsActive(r.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}