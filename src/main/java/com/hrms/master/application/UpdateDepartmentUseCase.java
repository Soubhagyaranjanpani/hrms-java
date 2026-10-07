package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.dto.DepartmentUpdateReq;
import com.hrms.master.infrastructure.BranchRepository;
import com.hrms.master.infrastructure.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateDepartmentUseCase {

    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;

    public ApiResponse<DefaultResponse> execute(DepartmentUpdateReq request) {

        Department dept = departmentRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        dept.setName(request.getName());
        dept.setBranch(branch);

        departmentRepository.save(dept);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Department Updated Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}