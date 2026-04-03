package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Department;
import com.hrms.master.dto.DepartmentResponse;
import com.hrms.master.infrastructure.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetDepartmentUseCase {

    private final DepartmentRepository departmentRepository;

    public ApiResponse<List<DepartmentResponse>> execute(Integer flag) {

        List<Department> list;

        if (flag == 1) {
            list = departmentRepository.findByIsActiveTrueAndIsDeletedFalse();
        } else {
            list = departmentRepository.findByIsDeletedFalse();
        }

        List<DepartmentResponse> response = list.stream().map(d -> {
            DepartmentResponse res = new DepartmentResponse();
            res.setId(d.getId());
            res.setCode(d.getCode());
            res.setName(d.getName());
            res.setBranchId(d.getBranch().getId());
            res.setBranchName(d.getBranch().getName());
            res.setIsActive(d.getIsActive());
            return res;
        }).collect(Collectors.toList());

        return ResponseUtils.createSuccessResponse(response, null);
    }
}