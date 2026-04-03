package com.hrms.employee.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.audit.application.AuditLogService;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.dto.EmployeeCreationReq;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.Role;
import com.hrms.master.infrastructure.BranchRepository;
import com.hrms.master.infrastructure.DepartmentRepository;
import com.hrms.master.infrastructure.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CreateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public ApiResponse<DefaultResponse> execute(EmployeeCreationReq request) {

        // 🔥 1. Validate email
        if (employeeRepository.existsByEmail(request.getEmail())) {
            return ResponseUtils.createFailureResponse(
                    null,
                    new TypeReference<>() {},
                    "Employee already exists",
                    400
            );
        }

        // 🔥 2. Fetch Role (MANDATORY)
        Role role = roleRepository.findById(request.getRoleId())
                .orElse(null);

        if (role == null) {
            return ResponseUtils.createFailureResponse(
                    null,
                    new TypeReference<>() {},
                    "Employee already exists",
                    400
            );
        }

        // 🔥 3. Fetch optional mappings
        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId()).orElse(null);
        }

        Branch branch = null;
        if (request.getBranchId() != null) {
            branch = branchRepository.findById(request.getBranchId()).orElse(null);
        }

        // 🔥 4. Create Employee
        Employee emp = new Employee();

        emp.setEmail(request.getEmail());
        emp.setPassword(passwordEncoder.encode(request.getPassword()));

        // 🔥 Name split
        if (request.getName() != null) {
            String[] parts = request.getName().trim().split(" ");
            emp.setFirstName(parts[0]);
            if (parts.length > 1) {
                emp.setLastName(parts[1]);
            }
        }

        emp.setPhone(request.getPhone());
        emp.setAddress(request.getAddress());
        emp.setProfilePicture(request.getProfilePicture());
        emp.setJoiningDate(request.getJoiningDate());

        emp.setRole(role);
        emp.setDepartment(department);
        emp.setBranch(branch);

        // 🔥 5. Generate employee code
        emp.setEmployeeCode(generateEmployeeCode());

        // 🔥 6. Save
        employeeRepository.save(emp);

        // 🔥 7. Audit
        auditLogService.log(
                "EMPLOYEE",
                emp.getId(),
                "CREATE",
                request.getEmail(),
                null,
                emp
        );

        // 🔥 8. Response
        DefaultResponse res = new DefaultResponse();
        res.setMsg("Employee created successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }

    // 🔥 Employee Code Generator
    private String generateEmployeeCode() {
        return "EMP" + (100000 + new Random().nextInt(900000));
    }
}
