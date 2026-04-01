package com.hrms.controller;


import com.hrms.model.Employee;
import com.hrms.request.EmployeeCreationReq;
import com.hrms.request.PasswordChangeReq;
import com.hrms.request.ResetPasswordReq;
import com.hrms.response.ApiResponse;
import com.hrms.response.EmployeeProfileResponse;
import com.hrms.security.DefaultResponse;
import com.hrms.security.JwtRequest;
import com.hrms.security.JwtResponse;
import com.hrms.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/employee")
@Tag(name = "Employee Controller", description = "APIs for managing employees in the HRMS system")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Operation(
            summary = "Create first employee (Super Admin)",
            description = "Creates the initial super admin employee. This endpoint does NOT require authentication."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee created successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"response\":{\"msg\":\"First Employee Created Successfully\"},\"status\":200,\"message\":\"success\"}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error or employee already exists"
            )
    })
    @PostMapping("/create-first")
    public ResponseEntity<ApiResponse<DefaultResponse>> createFirstEmployee(
            @Valid @RequestBody EmployeeCreationReq request) {
        return new ResponseEntity<>(employeeService.createFirstEmployee(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Employee Login",
            description = "Authenticate employee and receive JWT token for subsequent requests"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"response\":{\"jwtToken\":\"eyJhbGc...\",\"refreshToken\":\"eyJhbGc...\",\"username\":\"john@example.com\",\"employeeId\":1,\"name\":\"John Doe\",\"roleName\":\"ADMIN\"},\"status\":200,\"message\":\"success\"}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid username or password"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody JwtRequest request) {
        return new ResponseEntity<>(employeeService.login(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Create new employee",
            description = "Create a new employee. Requires ADMIN or SUPER_ADMIN role.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DefaultResponse>> createEmployee(
            @Valid @RequestBody EmployeeCreationReq request) {
        return new ResponseEntity<>(employeeService.createEmployee(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Get employee profile",
            description = "Get profile details of an employee by username (email or phone)"
    )
    @GetMapping("/profile/{username}")
    public ResponseEntity<ApiResponse<EmployeeProfileResponse>> getEmployeeProfile(
            @Parameter(description = "Username (email or phone number)", required = true, example = "john@example.com")
            @PathVariable String username) {
        return new ResponseEntity<>(employeeService.getEmployeeProfile(username), HttpStatus.OK);
    }

    @Operation(
            summary = "Get all employees",
            description = "Retrieve list of all employees. Requires ADMIN or SUPER_ADMIN role.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Employee>>> getAllEmployees() {
        return new ResponseEntity<>(employeeService.getAllEmployees(), HttpStatus.OK);
    }

    @Operation(
            summary = "Change password",
            description = "Change password for authenticated employee"
    )
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<DefaultResponse>> changePassword(
            @Valid @RequestBody PasswordChangeReq request) {
        return new ResponseEntity<>(employeeService.changePassword(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Reset password with OTP",
            description = "Reset password using OTP sent to registered email/phone"
    )
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<DefaultResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordReq request) {
        return new ResponseEntity<>(employeeService.resetPassword(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Send OTP",
            description = "Send OTP to registered email/phone for password reset"
    )
    @PostMapping("/send-otp/{username}")
    public ResponseEntity<ApiResponse<DefaultResponse>> sendOtp(
            @Parameter(description = "Username (email or phone)", required = true)
            @PathVariable String username) {
        return new ResponseEntity<>(employeeService.sendOtp(username), HttpStatus.OK);
    }

    @Operation(
            summary = "Logout",
            description = "Invalidate current JWT token",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<DefaultResponse>> logout(HttpServletRequest request) {
        return new ResponseEntity<>(employeeService.logout(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Get current logged-in employee",
            description = "Get details of currently authenticated employee",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<String>> getCurrentEmployee(Principal principal) {
        return new ResponseEntity<>(employeeService.getCurrentEmployee(principal), HttpStatus.OK);
    }
}