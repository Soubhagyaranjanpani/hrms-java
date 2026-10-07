package com.hrms.master.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.master.domain.Department;
import com.hrms.master.infrastructure.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeDepartmentStatusUseCase {

    private final DepartmentRepository departmentRepository;

    public ApiResponse<DefaultResponse> execute(Long id) {

        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        dept.setIsActive(!dept.getIsActive());

        departmentRepository.save(dept);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Department Status Updated");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}