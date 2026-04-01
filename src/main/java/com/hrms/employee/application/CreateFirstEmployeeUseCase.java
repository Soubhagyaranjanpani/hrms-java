package com.hrms.employee.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.dto.request.EmployeeCreationReq;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class CreateFirstEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<DefaultResponse> execute(EmployeeCreationReq request) {

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Employee already exists");
        }

        Employee emp = new Employee();
        emp.setEmail(request.getEmail());
        emp.setPassword(passwordEncoder.encode(request.getPassword()));
        emp.setFirstName(request.getName());

        employeeRepository.save(emp);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("First Employee Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}
