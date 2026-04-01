package com.hrms.service.Impl;




import com.hrms.model.Branch;
import com.hrms.model.Department;
import com.hrms.model.Employee;
import com.hrms.model.Role;
import com.hrms.repository.BranchRepository;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.RoleRepository;
import com.hrms.request.EmployeeCreationReq;
import com.hrms.request.EmployeeUpdateReq;
import com.hrms.request.PasswordChangeReq;
import com.hrms.request.ResetPasswordReq;
import com.hrms.response.ApiResponse;
import com.hrms.response.EmployeeProfileResponse;
import com.hrms.security.*;
import com.hrms.service.EmployeeService;
import com.hrms.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Override
    @Transactional
    public ApiResponse<JwtResponse> login(JwtRequest request) {
        // Validation
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "USERNAME CANNOT BE BLANK", 400);
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "PASSWORD CANNOT BE BLANK", 400);
        }

        try {
            // Find employee by email or phone
            Employee employee = employeeRepository.findByEmail(request.getUsername())
                    .orElseGet(() -> employeeRepository.findByPhone(request.getUsername()).orElse(null));

            if (employee == null) {
                return ResponseUtils.createFailureResponse(null, null, "INVALID USERNAME!", 401);
            }

            if (!employee.getIsActive()) {
                return ResponseUtils.createFailureResponse(null, null, "ACCOUNT IS DEACTIVATED. CONTACT ADMIN", 400);
            }

            // Authenticate
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(employee.getEmail(), request.getPassword());
            try {
                authenticationManager.authenticate(authentication);
            } catch (BadCredentialsException e) {
                return ResponseUtils.createFailureResponse(null, null, "INVALID USERNAME OR PASSWORD!", 401);
            }

            // Update last login
            employee.setLastLogin(LocalDateTime.now());
            employeeRepository.save(employee);

            // Generate tokens
            TokenWithExpiry accessTokenWithExpiry = jwtHelper.generateAccessTokenWithExpiry(employee);
            TokenWithExpiry refreshTokenWithExpiry = jwtHelper.generateRefreshTokenWithExpiry(employee);

            JwtResponse response = JwtResponse.builder()
                    .jwtToken(accessTokenWithExpiry.getToken())
                    .jwtTokenExpiry(accessTokenWithExpiry.getExpiryTime())
                    .refreshToken(refreshTokenWithExpiry.getToken())
                    .refreshTokenExpiry(refreshTokenWithExpiry.getExpiryTime())
                    .username(employee.getEmail())
                    .employeeId(employee.getId())
                    .name(employee.getName())
                    .email(employee.getEmail())
                    .roleId(employee.getRole() != null ? employee.getRole().getId() : null)
                    .roleName(employee.getRole() != null ? employee.getRole().getName() : null)
                    .build();

            return ResponseUtils.createSuccessResponse(response, null);

        } catch (Exception e) {
            logger.error("An error occurred while employee login", e);
            return ResponseUtils.createFailureResponse(null, null, "An error occurred while login", 500);
        }
    }

    @Override
    @Transactional
    public ApiResponse<DefaultResponse> createFirstEmployee(EmployeeCreationReq request) {
        DefaultResponse defaultResponse = new DefaultResponse();

        // Validations
        if (employeeRepository.existsByEmail(request.getEmail())) {
            return ResponseUtils.createFailureResponse(null, null, "EMPLOYEE ALREADY EXISTS WITH THIS EMAIL", 400);
        }
        if (employeeRepository.existsByPhone(request.getPhone())) {
            return ResponseUtils.createFailureResponse(null, null, "EMPLOYEE ALREADY EXISTS WITH THIS PHONE", 400);
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "EMAIL CANNOT BE BLANK", 400);
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "PASSWORD CANNOT BE BLANK", 400);
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "NAME CANNOT BE BLANK", 400);
        }

        try {
            Employee employee = new Employee();
            employee.setEmail(request.getEmail());
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
            employee.setName(request.getName());
            employee.setPhone(request.getPhone());
            employee.setJoiningDate(request.getJoiningDate());
            employee.setAddress(request.getAddress());
            employee.setProfilePicture(request.getProfilePicture());
            employee.setIsActive(true);
            employee.setCreatedOn(LocalDateTime.now());

            // Set role if provided
            if (request.getRoleId() != null) {
                roleRepository.findById(request.getRoleId()).ifPresent(employee::setRole);
            }

            // Set department if provided
            if (request.getDepartmentId() != null) {
                departmentRepository.findById(request.getDepartmentId()).ifPresent(employee::setDepartment);
            }

            // Set branch if provided
            if (request.getBranchId() != null) {
                branchRepository.findById(request.getBranchId()).ifPresent(employee::setBranch);
            }

            employeeRepository.save(employee);

            defaultResponse.setMsg("First Employee Created Successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, null);

        } catch (Exception e) {
            logger.error("An error occurred while creating first employee", e);
            return ResponseUtils.createFailureResponse(null, null, "An error occurred while creating employee", 500);
        }
    }

    @Override
    @Transactional
    public ApiResponse<DefaultResponse> createEmployee(EmployeeCreationReq request) {
        DefaultResponse defaultResponse = new DefaultResponse();

        // Validations
        if (employeeRepository.existsByEmail(request.getEmail())) {
            return ResponseUtils.createFailureResponse(null, null, "EMPLOYEE ALREADY EXISTS WITH THIS EMAIL", 400);
        }
        if (employeeRepository.existsByPhone(request.getPhone())) {
            return ResponseUtils.createFailureResponse(null, null, "EMPLOYEE ALREADY EXISTS WITH THIS PHONE", 400);
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "EMAIL CANNOT BE BLANK", 400);
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "PASSWORD CANNOT BE BLANK", 400);
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "NAME CANNOT BE BLANK", 400);
        }

        try {
            Employee employee = new Employee();
            employee.setEmail(request.getEmail());
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
            employee.setName(request.getName());
            employee.setPhone(request.getPhone());
            employee.setJoiningDate(request.getJoiningDate());
            employee.setAddress(request.getAddress());
            employee.setProfilePicture(request.getProfilePicture());
            employee.setIsActive(true);
            employee.setCreatedOn(LocalDateTime.now());

            // Get current logged in user
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Employee created by: {}", currentUsername);

            // Set role
            if (request.getRoleId() != null) {
                Role role = roleRepository.findById(request.getRoleId())
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                employee.setRole(role);
            }

            // Set department
            if (request.getDepartmentId() != null) {
                Department department = departmentRepository.findById(request.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                employee.setDepartment(department);
            }

            // Set branch
            if (request.getBranchId() != null) {
                Branch branch = branchRepository.findById(request.getBranchId())
                        .orElseThrow(() -> new RuntimeException("Branch not found"));
                employee.setBranch(branch);
            }

            employeeRepository.save(employee);

            defaultResponse.setMsg("Employee Created Successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, null);

        } catch (Exception e) {
            logger.error("An error occurred while creating employee", e);
            return ResponseUtils.createFailureResponse(null, null, "An error occurred while creating employee", 500);
        }
    }

    @Override
    @Transactional
    public ApiResponse<DefaultResponse> updateEmployee(EmployeeUpdateReq request) {
        DefaultResponse defaultResponse = new DefaultResponse();

        if (request.getId() == null) {
            return ResponseUtils.createFailureResponse(null, null, "EMPLOYEE ID CANNOT BE NULL", 400);
        }

        Employee employee = employeeRepository.findById(request.getId()).orElse(null);
        if (employee == null) {
            return ResponseUtils.createFailureResponse(null, null, "EMPLOYEE NOT FOUND", 400);
        }

        try {
            if (request.getName() != null) employee.setName(request.getName());
            if (request.getPhone() != null) employee.setPhone(request.getPhone());
            if (request.getAddress() != null) employee.setAddress(request.getAddress());
            if (request.getProfilePicture() != null) employee.setProfilePicture(request.getProfilePicture());
            if (request.getIsActive() != null) employee.setIsActive(request.getIsActive());

            if (request.getRoleId() != null) {
                roleRepository.findById(request.getRoleId()).ifPresent(employee::setRole);
            }
            if (request.getDepartmentId() != null) {
                departmentRepository.findById(request.getDepartmentId()).ifPresent(employee::setDepartment);
            }
            if (request.getBranchId() != null) {
                branchRepository.findById(request.getBranchId()).ifPresent(employee::setBranch);
            }

            employeeRepository.save(employee);
            defaultResponse.setMsg("Employee Updated Successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, null);

        } catch (Exception e) {
            logger.error("An error occurred while updating employee", e);
            return ResponseUtils.createFailureResponse(null, null, "An error occurred while updating employee", 500);
        }
    }

    @Override
    public ApiResponse<EmployeeProfileResponse> getEmployeeProfile(String username) {
        if (username == null || username.isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "USERNAME CANNOT BE BLANK", 400);
        }

        Employee employee = employeeRepository.findByEmailAndIsActiveTrue(username)
                .orElseGet(() -> employeeRepository.findByPhoneAndIsActiveTrue(username).orElse(null));

        if (employee == null) {
            return ResponseUtils.createFailureResponse(null, null, "ACTIVE EMPLOYEE NOT FOUND", 400);
        }

        EmployeeProfileResponse response = new EmployeeProfileResponse();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setPhone(employee.getPhone());
        response.setJoiningDate(employee.getJoiningDate());
        response.setAddress(employee.getAddress());
        response.setProfilePicture(employee.getProfilePicture());
        response.setLastLogin(employee.getLastLogin());

        if (employee.getRole() != null) {
            response.setRoleName(employee.getRole().getName());
        }
        if (employee.getDepartment() != null) {
            response.setDepartmentName(employee.getDepartment().getName());
        }
        if (employee.getBranch() != null) {
            response.setBranchName(employee.getBranch().getName());
        }

        return ResponseUtils.createSuccessResponse(response, null);
    }

    @Override
    public ApiResponse<List<Employee>> getAllEmployees() {
        try {
            List<Employee> employees = employeeRepository.findAll();
            if (employees.isEmpty()) {
                return ResponseUtils.createFailureResponse(null, null, "No employees found", 400);
            }
            return ResponseUtils.createSuccessResponse(employees, null);
        } catch (Exception e) {
            logger.error("An error occurred while fetching employees", e);
            return ResponseUtils.createFailureResponse(null, null, "Error while fetching employees", 500);
        }
    }

    @Override
    public ApiResponse<Employee> getEmployeeById(Long id) {
        if (id == null) {
            return ResponseUtils.createFailureResponse(null, null, "EMPLOYEE ID CANNOT BE NULL", 400);
        }

        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "EMPLOYEE NOT FOUND", 404);
        }

        return ResponseUtils.createSuccessResponse(employee.get(), null);
    }

    @Override
    @Transactional
    public ApiResponse<DefaultResponse> changePassword(PasswordChangeReq request) {
        DefaultResponse response = new DefaultResponse();

        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "USERNAME CANNOT BE BLANK", 400);
        }
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "CURRENT PASSWORD CANNOT BE BLANK", 400);
        }
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "NEW PASSWORD CANNOT BE BLANK", 400);
        }

        Employee employee = employeeRepository.findByEmail(request.getUsername())
                .orElseGet(() -> employeeRepository.findByPhone(request.getUsername()).orElse(null));

        if (employee == null) {
            return ResponseUtils.createFailureResponse(null, null, "INVALID USER", 400);
        }

        if (!employee.getIsActive()) {
            return ResponseUtils.createFailureResponse(null, null, "ACCOUNT IS DEACTIVATED", 400);
        }

        try {
            boolean isPasswordMatch = passwordEncoder.matches(request.getCurrentPassword(), employee.getPassword());
            boolean isSamePassword = passwordEncoder.matches(request.getNewPassword(), employee.getPassword());

            if (isPasswordMatch) {
                if (isSamePassword) {
                    response.setMsg("CURRENT PASSWORD & NEW PASSWORD CANNOT BE SAME");
                } else {
                    employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    employeeRepository.save(employee);
                    response.setMsg("PASSWORD CHANGED SUCCESSFULLY");
                }
            } else {
                response.setMsg("CURRENT PASSWORD NOT MATCHED");
            }
        } catch (Exception e) {
            logger.error("An error occurred while changing password", e);
            return ResponseUtils.createFailureResponse(null, null, "Error while changing password", 500);
        }

        return ResponseUtils.createSuccessResponse(response, null);
    }

    @Override
    @Transactional
    public ApiResponse<DefaultResponse> resetPassword(ResetPasswordReq request) {
        DefaultResponse response = new DefaultResponse();

        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "USERNAME CANNOT BE BLANK", 400);
        }
        if (request.getOtp() == null || request.getOtp().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "OTP CANNOT BE BLANK", 400);
        }
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "NEW PASSWORD CANNOT BE BLANK", 400);
        }
        if (request.getReEnterPassword() == null || request.getReEnterPassword().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "RE-ENTER PASSWORD CANNOT BE BLANK", 400);
        }
        if (!request.getNewPassword().equals(request.getReEnterPassword())) {
            return ResponseUtils.createFailureResponse(null, null, "NEW PASSWORD & RE-ENTER PASSWORD MUST BE SAME", 400);
        }

        Employee employee = employeeRepository.findByEmail(request.getUsername())
                .orElseGet(() -> employeeRepository.findByPhone(request.getUsername()).orElse(null));

        if (employee == null) {
            return ResponseUtils.createFailureResponse(null, null, "INVALID USER", 400);
        }

        if (!employee.getIsActive()) {
            return ResponseUtils.createFailureResponse(null, null, "ACCOUNT IS DEACTIVATED", 400);
        }

        String userOtp = employee.getTempOtp();
        if (userOtp == null || userOtp.isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "OTP NOT FOUND. PLEASE REQUEST NEW OTP", 400);
        }

        LocalDateTime otpExpiry = employee.getOtpExpiryTime();
        if (otpExpiry == null || otpExpiry.isBefore(LocalDateTime.now())) {
            response.setMsg("Your OTP has Expired. Please request new OTP");
            return ResponseUtils.createSuccessResponse(response, null);
        }

        try {
            if (userOtp.equals(request.getOtp())) {
                employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
                employee.setTempOtp(null);
                employee.setOtpExpiryTime(null);
                employeeRepository.save(employee);
                response.setMsg("Your Password has been Successfully Reset");
            } else {
                response.setMsg("INVALID OTP");
            }
        } catch (Exception e) {
            logger.error("An error occurred while resetting password", e);
            return ResponseUtils.createFailureResponse(null, null, "Error while resetting password", 500);
        }

        return ResponseUtils.createSuccessResponse(response, null);
    }

    @Override
    @Transactional
    public ApiResponse<DefaultResponse> activeInactiveEmployee(Long id, Boolean isActive) {
        DefaultResponse response = new DefaultResponse();

        if (id == null) {
            return ResponseUtils.createFailureResponse(null, null, "EMPLOYEE ID CANNOT BE NULL", 400);
        }

        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null) {
            return ResponseUtils.createFailureResponse(null, null, "EMPLOYEE NOT FOUND", 404);
        }

        try {
            employee.setIsActive(isActive);
            employeeRepository.save(employee);
            String status = isActive ? "Activated" : "Deactivated";
            response.setMsg("Employee " + status + " Successfully");
            return ResponseUtils.createSuccessResponse(response, null);
        } catch (Exception e) {
            logger.error("An error occurred while updating employee status", e);
            return ResponseUtils.createFailureResponse(null, null, "Error while updating employee status", 500);
        }
    }

    @Override
    public ApiResponse<DefaultResponse> logout(HttpServletRequest request) {
        DefaultResponse response = new DefaultResponse();
        String token = extractTokenFromRequest(request);

        if (token == null) {
            return ResponseUtils.createFailureResponse(null, null, "Invalid token", 400);
        }

        long expirationTime = jwtHelper.getExpirationTime(token);
        tokenBlacklistService.addToBlacklist(token, expirationTime);
        response.setMsg("Logged out successfully");

        return ResponseUtils.createSuccessResponse(response, null);
    }

    @Override
    public ApiResponse<String> getCurrentEmployee(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "USERNAME CANNOT BE BLANK", 400);
        }

        Employee employee = employeeRepository.findByEmail(principal.getName()).orElse(null);
        if (employee == null) {
            return ResponseUtils.createFailureResponse(null, null, "CURRENT EMPLOYEE NOT FOUND", 400);
        }

        return ResponseUtils.createSuccessResponse(employee.getEmail() + " (" + employee.getName() + ")", null);
    }

    @Override
    @Transactional
    public ApiResponse<DefaultResponse> sendOtp(String username) {
        DefaultResponse response = new DefaultResponse();

        if (username == null || username.isEmpty()) {
            return ResponseUtils.createFailureResponse(null, null, "USERNAME CANNOT BE BLANK", 400);
        }

        Employee employee = employeeRepository.findByEmail(username)
                .orElseGet(() -> employeeRepository.findByPhone(username).orElse(null));

        if (employee == null) {
            return ResponseUtils.createFailureResponse(null, null, "INVALID USER", 400);
        }

        if (!employee.getIsActive()) {
            return ResponseUtils.createFailureResponse(null, null, "ACCOUNT IS DEACTIVATED", 400);
        }

        try {
            // Generate 6-digit OTP
            String otp = String.format("%06d", new Random().nextInt(999999));
            employee.setTempOtp(otp);
            employee.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
            employeeRepository.save(employee);

            // In production, send OTP via email/SMS here
            logger.info("OTP for {}: {}", employee.getEmail(), otp);

            response.setMsg("OTP sent successfully to your registered email/phone");
            return ResponseUtils.createSuccessResponse(response, null);
        } catch (Exception e) {
            logger.error("An error occurred while sending OTP", e);
            return ResponseUtils.createFailureResponse(null, null, "Error while sending OTP", 500);
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
