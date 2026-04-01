package com.hrms.service;




import com.hrms.model.Employee;
import com.hrms.request.EmployeeCreationReq;
import com.hrms.request.EmployeeUpdateReq;
import com.hrms.request.PasswordChangeReq;
import com.hrms.request.ResetPasswordReq;
import com.hrms.response.ApiResponse;
import com.hrms.response.EmployeeProfileResponse;
import com.hrms.security.DefaultResponse;
import com.hrms.security.JwtRequest;
import com.hrms.security.JwtResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.List;

public interface EmployeeService {

    ApiResponse<JwtResponse> login(JwtRequest request);

    ApiResponse<DefaultResponse> createFirstEmployee(EmployeeCreationReq request);

    ApiResponse<DefaultResponse> createEmployee(EmployeeCreationReq request);

    ApiResponse<DefaultResponse> updateEmployee(EmployeeUpdateReq request);

    ApiResponse<EmployeeProfileResponse> getEmployeeProfile(String username);

    ApiResponse<List<Employee>> getAllEmployees();

    ApiResponse<Employee> getEmployeeById(Long id);

    ApiResponse<DefaultResponse> changePassword(PasswordChangeReq request);

    ApiResponse<DefaultResponse> resetPassword(ResetPasswordReq request);

    ApiResponse<DefaultResponse> activeInactiveEmployee(Long id, Boolean isActive);

    ApiResponse<DefaultResponse> logout(HttpServletRequest request);

    ApiResponse<String> getCurrentEmployee(Principal principal);

    ApiResponse<DefaultResponse> sendOtp(String username);
}
