package com.hrms.employee.application;

import com.hrms.audit.application.AuditLogService;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.dto.EmployeeCreationReq;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.Role;
import com.hrms.master.infrastructure.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class CreateFirstEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final RoleRepository roleRepository;

    public ApiResponse<DefaultResponse> execute(EmployeeCreationReq request) {

        // 🔹 1. Check existing employee
        if (employeeRepository.existsByEmail(request.getEmail())) {
            return ResponseUtils.createFailureResponse(null,null,"Employee already exists",400);
        }

        // 🔹 2. Fetch Role (MANDATORY)
        Role role = roleRepository.findById(request.getRoleId()).orElse(null);

        if (role == null) {
            return ResponseUtils.createFailureResponse(null,null,"Role not found",400);
        }

        // 🔹 3. Create employee
        Employee emp = new Employee();

        emp.setEmail(request.getEmail());
        emp.setPassword(passwordEncoder.encode(request.getPassword()));

        // 🔥 Name handling
        if (request.getName() != null) {
            String[] parts = request.getName().trim().split(" ");
            emp.setFirstName(parts[0]);
            if (parts.length > 1) {
                emp.setLastName(parts[1]);
            }
        }

        // 🔥 CRITICAL FIXES
        emp.setEmployeeCode(generateEmployeeCode());
        emp.setRole(role);

        // 🔹 4. Save
        employeeRepository.save(emp);

        // 🔹 5. Audit
        auditLogService.log(
                "EMPLOYEE",
                emp.getId(),
                "CREATE",
                request.getEmail(),
                null,
                emp
        );

        // 🔹 6. Response
        DefaultResponse res = new DefaultResponse();
        res.setMsg("First Employee Created Successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }

    // 🔥 Employee Code Generator
    private String generateEmployeeCode() {
        return "EMP" + (100000 + new Random().nextInt(900000));
    }
}
