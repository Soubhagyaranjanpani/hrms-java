package com.hrms.employee.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.dto.response.EmployeeProfileResponse;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetEmployeeProfileUseCase {

    private final EmployeeRepository employeeRepository;

    public ApiResponse<EmployeeProfileResponse> execute(String username) {

        Employee emp = employeeRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Not found"));

        EmployeeProfileResponse res = new EmployeeProfileResponse();
        res.setId(emp.getId());
        res.setEmail(emp.getEmail());

        return ResponseUtils.createSuccessResponse(res, null);
    }
}
