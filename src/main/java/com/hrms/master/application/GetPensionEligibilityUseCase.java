package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.PensionEligibility;
import com.hrms.master.dto.PensionEligibilityResponse;
import com.hrms.master.infrastructure.PensionEligibilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetPensionEligibilityUseCase {

    private final PensionEligibilityRepository repository;

    public ApiResponse<List<PensionEligibilityResponse>> execute(Integer flag) {

        List<PensionEligibility> list;

        if (flag == 1) {
            list = repository.findByIsActiveTrue();
        } else {
            list = repository.findAll();
        }

        List<PensionEligibilityResponse> response = list.stream().map(r -> {
            PensionEligibilityResponse res = new PensionEligibilityResponse();
            res.setId(r.getId());
            res.setName(r.getName());
            res.setIsActive(r.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}