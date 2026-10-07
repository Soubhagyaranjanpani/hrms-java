package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.PenaltyType;
import com.hrms.master.dto.PenaltyTypeResponse;
import com.hrms.master.infrastructure.PenaltyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetPenaltyTypeUseCase {

    private final PenaltyTypeRepository penaltyTypeRepository;

    public ApiResponse<List<PenaltyTypeResponse>> execute(Integer flag) {

        List<PenaltyType> list;

        if (flag == 1) {
            list = penaltyTypeRepository.findByIsActiveTrue();
        } else {
            list = penaltyTypeRepository.findAll();
        }

        List<PenaltyTypeResponse> response = list.stream().map(p -> {
            PenaltyTypeResponse res = new PenaltyTypeResponse();
            res.setId(p.getId());
            res.setName(p.getName());
            res.setIsActive(p.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}