package com.hrms.employee.application;

import com.hrms.audit.application.AuditLogService;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.JwtHelper;
import com.hrms.common.security.JwtRequest;
import com.hrms.common.security.JwtResponse;
import com.hrms.common.security.TokenWithExpiry;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtHelper jwtHelper;
    private final AuditLogService auditLogService;

    public ApiResponse<JwtResponse> execute(JwtRequest request) {

        Employee employee = employeeRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid user"));
        System.out.println(new BCryptPasswordEncoder().encode("pass"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        employee.getEmail(),
                        request.getPassword()
                )
        );

        TokenWithExpiry access = jwtHelper.generateAccessTokenWithExpiry(employee);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(access.getToken())
                .username(employee.getEmail())
                .employeeId(employee.getId())
                .build();
        auditLogService.log(
                "AUTH",
                employee.getId(),
                "LOGIN",
                employee.getEmail(),
                null,
                "Login success"
        );

        return ResponseUtils.createSuccessResponse(response, null);
    }
}
