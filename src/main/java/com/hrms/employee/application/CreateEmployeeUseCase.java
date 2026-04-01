package com.hrms.employee.application;

import com.hrms.audit.application.AuditLogService;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.dto.EmployeeCreationReq;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    public ApiResponse<DefaultResponse> execute(EmployeeCreationReq request) {

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Employee exists");
        }

        Employee emp = new Employee();
        emp.setEmail(request.getEmail());
        emp.setPassword(passwordEncoder.encode(request.getPassword()));
        emp.setFirstName(request.getName());

        employeeRepository.save(emp);
        auditLogService.log(
                "EMPLOYEE",
                emp.getId(),
                "CREATE",
                request.getEmail(),
                null,
                emp
        );

        DefaultResponse res = new DefaultResponse();
        res.setMsg("Employee Created");

        return ResponseUtils.createSuccessResponse(res, null);
    }
}
