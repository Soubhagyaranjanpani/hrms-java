package com.hrms.employee.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeleteEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    public ApiResponse<String> execute(Long id) {

        Optional<Employee> optionalEmployee =
                employeeRepository.findByIdAndIsDeletedFalse(id);

        if (!optionalEmployee.isPresent()) {
            return ResponseUtils.createFailureResponse("Not found",null,"Employee not found",404);
        }

        Employee employee = optionalEmployee.get();

        // 🔥 Soft delete
        employee.setIsDeleted(true);
        employee.setIsActive(false);

        employeeRepository.save(employee);

        return ResponseUtils.createSuccessResponse("Employee deleted successfully", null);
    }
}
