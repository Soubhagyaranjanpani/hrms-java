package com.hrms.employee.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class GetCurrentEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    public ApiResponse<String> execute(Principal principal) {

        if (principal == null) {
            throw new RuntimeException("No authenticated user");
        }

        Employee emp = employeeRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String result = emp.getEmail() + " (" + emp.getFirstName() + ")";

        return ResponseUtils.createSuccessResponse(result, null);
    }
}
