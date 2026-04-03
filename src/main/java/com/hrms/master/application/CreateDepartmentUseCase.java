package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.dto.DepartmentCreateReq;
import com.hrms.master.infrastructure.BranchRepository;
import com.hrms.master.infrastructure.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateDepartmentUseCase {

    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;

    public ApiResponse<DefaultResponse> execute(DepartmentCreateReq request) {

        if (departmentRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Department code already exists");
        }

        if (departmentRepository.existsByNameAndBranch_Id(request.getName(), request.getBranchId())) {
            throw new RuntimeException("Department already exists in this branch");
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        Department dept = new Department();
        dept.setCode(request.getCode());
        dept.setName(request.getName());
        dept.setBranch(branch);

        departmentRepository.save(dept);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Department Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}