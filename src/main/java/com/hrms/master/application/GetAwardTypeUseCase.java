package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.AwardType;
import com.hrms.master.dto.AwardTypeResponse;
import com.hrms.master.infrastructure.AwardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAwardTypeUseCase {

    private final AwardTypeRepository awardTypeRepository;

    public ApiResponse<List<AwardTypeResponse>> execute(Integer flag) {

        List<AwardType> list;

        if (flag == 1) {
            list = awardTypeRepository.findByIsActiveTrue();
        } else {
            list = awardTypeRepository.findAll();
        }

        List<AwardTypeResponse> response = list.stream().map(a -> {
            AwardTypeResponse res = new AwardTypeResponse();
            res.setId(a.getId());
            res.setName(a.getName());
            res.setIsActive(a.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}