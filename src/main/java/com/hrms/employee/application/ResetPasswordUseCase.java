package com.hrms.employee.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.dto.request.ResetPasswordReq;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResetPasswordUseCase {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<DefaultResponse> execute(ResetPasswordReq request) {

        Employee emp = employeeRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!request.getNewPassword().equals(request.getReEnterPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        if (emp.getOtpExpiryTime() == null ||
                emp.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!request.getOtp().equals(emp.getTempOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        emp.setPassword(passwordEncoder.encode(request.getNewPassword()));
        emp.setTempOtp(null);
        emp.setOtpExpiryTime(null);

        employeeRepository.save(emp);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Password reset successful");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}
