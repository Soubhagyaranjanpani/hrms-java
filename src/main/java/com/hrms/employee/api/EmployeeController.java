package com.hrms.employee.api;

import com.hrms.employee.application.*;
import com.hrms.employee.dto.*;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final GetAllEmployeesUseCase getAllEmployeesUseCase;
    private final DeleteEmployeeUseCase deleteEmployeeUseCase;
    private final UpdateEmployeeUseCase updateEmployeeUseCase;

    // =========================
    // ADMIN APIs
    // =========================

//    @PreAuthorize("hasRole('ADMIN')")

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EmployeeProfileResponse>>> getAllEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                getAllEmployeesUseCase.execute(name, isActive, page, size)
        );
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(deleteEmployeeUseCase.execute(id));
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeUpdateReq request) {

        return ResponseEntity.ok(updateEmployeeUseCase.execute(id, request));
    }
//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DefaultResponse>> createEmployee(
            @RequestBody EmployeeCreationReq request) {
        return ResponseEntity.ok(createEmployeeUseCase.execute(request));
    }

    // =========================
    // PUBLIC APIs
    // =========================

    @PostMapping("/create-first")
    public ResponseEntity<ApiResponse<DefaultResponse>> createFirstEmployee(
            @RequestBody EmployeeCreationReq request) {
        return ResponseEntity.ok(createFirstEmployeeUseCase.execute(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody JwtRequest request) {
        return ResponseEntity.ok(loginEmployeeUseCase.execute(request));
    }

    @PostMapping("/send-otp/{username}")
    public ResponseEntity<ApiResponse<DefaultResponse>> sendOtp(
            @PathVariable String username) {
        return ResponseEntity.ok(sendOtpUseCase.execute(username));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<DefaultResponse>> resetPassword(
            @RequestBody ResetPasswordReq request) {
        return ResponseEntity.ok(resetPasswordUseCase.execute(request));
    }

    // =========================
    // AUTHENTICATED USER APIs
    // =========================

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<DefaultResponse>> changePassword(
            @RequestBody PasswordChangeReq request) {
        return ResponseEntity.ok(changePasswordUseCase.execute(request));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<DefaultResponse>> logout(
            HttpServletRequest request) {
        return ResponseEntity.ok(logoutUseCase.execute(request));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<String>> getCurrent(Principal principal) {
        return ResponseEntity.ok(getCurrentEmployeeUseCase.execute(principal));
    }

    // =========================
    // SELF + ADMIN ACCESS
    // =========================

    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    @GetMapping("/profile/{username}")
    public ResponseEntity<ApiResponse<EmployeeProfileResponse>> getProfile(
            @PathVariable String username) {
        return ResponseEntity.ok(getEmployeeProfileUseCase.execute(username));
    }
}
