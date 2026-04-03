package com.hrms.employee.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    public ApiResponse<String> execute(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElse(null);

        if (employee == null || Boolean.TRUE.equals(employee.getIsDeleted())) {
            return ResponseUtils.createFailureResponse("Employee not found",null,"Employee not found",404);
        }

        employee.setIsDeleted(true);
        employee.setIsActive(false);

        employeeRepository.save(employee);

        return ResponseUtils.createSuccessResponse("Employee deleted successfully",null);
    }
}
