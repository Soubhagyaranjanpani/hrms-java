package com.hrms.employment_type.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employment_type.domain.EmploymentType;                 // fixed
import com.hrms.employment_type.dto.EmploymentTypeResponse;          // fixed
import com.hrms.employment_type.infrastructure.EmploymentTypeRepository; // fixed
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetEmploymentTypeUseCase {

    private final EmploymentTypeRepository employmentTypeRepository;

    public ApiResponse<List<EmploymentTypeResponse>> execute(Integer flag) {

        List<EmploymentType> list;

        if (flag == 1) {
            list = employmentTypeRepository.findByIsActiveTrue();
        } else {
            list = employmentTypeRepository.findAll();
        }

        List<EmploymentTypeResponse> response = list.stream().map(t -> {
            EmploymentTypeResponse res = new EmploymentTypeResponse();
            res.setId(t.getId());
            res.setName(t.getName());
            res.setIsActive(t.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}