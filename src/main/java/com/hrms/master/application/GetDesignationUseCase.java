package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Designation;
import com.hrms.master.dto.DesignationResponse;
import com.hrms.master.infrastructure.DesignationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetDesignationUseCase {

    private final DesignationRepository designationRepository;

    public ApiResponse<List<DesignationResponse>> execute(Integer flag) {

        List<Designation> list;

        if (flag == 1) {
            list = designationRepository.findByIsActiveTrue();
        } else {
            list = designationRepository.findAll();
        }

        List<DesignationResponse> response = list.stream().map(d -> {
            DesignationResponse res = new DesignationResponse();
            res.setId(d.getId());
            res.setName(d.getName());
            res.setIsActive(d.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}