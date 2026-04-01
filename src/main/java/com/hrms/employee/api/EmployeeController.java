package com.hrms.employee.api;

import com.hrms.employee.application.*;
import com.hrms.employee.dto.request.*;
import com.hrms.employee.dto.response.EmployeeProfileResponse;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final CreateFirstEmployeeUseCase createFirstEmployeeUseCase;
    private final LoginEmployeeUseCase loginEmployeeUseCase;
    private final CreateEmployeeUseCase createEmployeeUseCase;
    private final GetEmployeeProfileUseCase getEmployeeProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final SendOtpUseCase sendOtpUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetCurrentEmployeeUseCase getCurrentEmployeeUseCase;

    @PostMapping("/create-first")
    public ResponseEntity<ApiResponse<DefaultResponse>> createFirstEmployee(
            @RequestBody EmployeeCreationReq request) {
        return ResponseEntity.ok(createFirstEmployeeUseCase.execute(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody JwtRequest request) {
        return ResponseEntity.ok(loginEmployeeUseCase.execute(request));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DefaultResponse>> createEmployee(
            @RequestBody EmployeeCreationReq request) {
        return ResponseEntity.ok(createEmployeeUseCase.execute(request));
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<ApiResponse<EmployeeProfileResponse>> getProfile(
            @PathVariable String username) {
        return ResponseEntity.ok(getEmployeeProfileUseCase.execute(username));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<DefaultResponse>> changePassword(
            @RequestBody PasswordChangeReq request) {
        return ResponseEntity.ok(changePasswordUseCase.execute(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<DefaultResponse>> resetPassword(
            @RequestBody ResetPasswordReq request) {
        return ResponseEntity.ok(resetPasswordUseCase.execute(request));
    }

    @PostMapping("/send-otp/{username}")
    public ResponseEntity<ApiResponse<DefaultResponse>> sendOtp(
            @PathVariable String username) {
        return ResponseEntity.ok(sendOtpUseCase.execute(username));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<DefaultResponse>> logout(
            HttpServletRequest request) {
        return ResponseEntity.ok(logoutUseCase.execute(request));
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<String>> getCurrent(Principal principal) {
        return ResponseEntity.ok(getCurrentEmployeeUseCase.execute(principal));
    }
}
