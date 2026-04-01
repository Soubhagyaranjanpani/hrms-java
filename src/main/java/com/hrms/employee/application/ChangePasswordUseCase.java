package com.hrms.employee.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.dto.request.PasswordChangeReq;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<DefaultResponse> execute(PasswordChangeReq req) {

        Employee emp = employeeRepository.findByEmail(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), emp.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        emp.setPassword(passwordEncoder.encode(req.getNewPassword()));
        employeeRepository.save(emp);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Password changed");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}
