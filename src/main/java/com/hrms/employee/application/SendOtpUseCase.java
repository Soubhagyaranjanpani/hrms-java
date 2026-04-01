package com.hrms.employee.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SendOtpUseCase {

    private final EmployeeRepository employeeRepository;

    public ApiResponse<DefaultResponse> execute(String username) {

        Employee emp = employeeRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = String.format("%06d", new Random().nextInt(999999));

        emp.setTempOtp(otp);
        emp.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));

        employeeRepository.save(emp);

        DefaultResponse res = new DefaultResponse();
        res.setMsg("OTP sent");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}
