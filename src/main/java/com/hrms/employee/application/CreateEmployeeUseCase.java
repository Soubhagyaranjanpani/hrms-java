package com.hrms.employee.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeGrade;
import com.hrms.employee.dto.EmployeeCreationReq;
import com.hrms.employee.infrastructure.EmployeeGradeRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.leave.application.InitializeLeaveBalanceUseCase;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.Role;
import com.hrms.master.infrastructure.BranchRepository;
import com.hrms.master.infrastructure.DepartmentRepository;
import com.hrms.master.infrastructure.DesignationRepository;
import com.hrms.master.infrastructure.RoleRepository;
import com.hrms.serviceBook.domain.ServiceBook;
import com.hrms.serviceBook.infrastructure.ServiceBookRepository;
import com.hrms.service_history.domain.ServiceHistory;
import com.hrms.service_history.infrastructure.ServiceHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class CreateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;
    private final InitializeLeaveBalanceUseCase initializeLeaveBalanceUseCase;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeGradeRepository gradeRepo;
    private final DesignationRepository designationRepository;
    private final ServiceBookRepository serviceBookRepository;
    private final ServiceHistoryRepository serviceHistoryRepository;
    private final AuditService auditService;  // Only one AuditService injection

    @Transactional
    public ApiResponse<DefaultResponse> execute(EmployeeCreationReq request) {

        // 1. Validate email
        if (employeeRepository.existsByEmail(request.getEmail())) {
            return ResponseUtils.createFailureResponse(
                    null,
                    new TypeReference<>() {},
                    "Employee with this email already exists",
                    400
            );
        }

        // 2. Fetch Role (MANDATORY)
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Invalid Role ID"));

        // 3. Fetch optional mappings
        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Invalid Department Id"));
        }

        Branch branch = null;
        if (request.getBranchId() != null) {
            branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Invalid Branch Id"));
        }

        // 4. Create Employee
        Employee emp = new Employee();
        emp.setEmail(request.getEmail());
        emp.setPassword(passwordEncoder.encode(request.getPassword()));

        // Name split
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String[] parts = request.getName().trim().split("\\s+", 2);
            emp.setFirstName(parts[0]);
            if (parts.length > 1) {
                emp.setLastName(parts[1]);
            }
        }

        emp.setPhone(request.getPhone());
        emp.setAddress(request.getAddress());
        emp.setProfilePicture(request.getProfilePicture());
        emp.setJoiningDate(request.getJoiningDate());
        emp.setBankAccount(request.getBankAccount());
        emp.setUan(request.getUan());
        emp.setPan(request.getPan());

        if (request.getGradeId() != null) {
            EmployeeGrade grade = gradeRepo.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Grade not found"));
            emp.setGrade(grade);
        }

        emp.setRole(role);
        emp.setDepartment(department);
        emp.setBranch(branch);

        // 5. Generate employee code
        emp.setEmployeeCode(generateEmployeeCode());

        emp.setDesignation(designationRepository
                .findById(request.getDesignationId())
                .orElseThrow(() -> new RuntimeException("Invalid Designation ID")));

        // 6. Save Employee
        Employee savedEmployee = employeeRepository.save(emp);

        // 7. Create Service Book
        ServiceBook book = new ServiceBook();
        book.setEmployee(savedEmployee);
        String serviceBookNo = generateRandomServiceBookNo();
        book.setServiceBookNo(serviceBookNo);
        book.setServiceBookName(savedEmployee.getEmployeeCode() + "-" + serviceBookNo);
        book.setCreatedBy(savedEmployee.getCreatedBy());
        book.setUpdatedBy(savedEmployee.getUpdatedBy());

        ServiceBook savedBook = serviceBookRepository.save(book);

        // 8. Create Service History
        ServiceHistory history = new ServiceHistory();
        history.setServiceBook(savedBook);
        history.setToDesignation(savedEmployee.getDesignation());
        history.setToBranch(savedEmployee.getBranch());
        history.setToDepartment(savedEmployee.getDepartment());
        history.setFormDate(savedEmployee.getJoiningDate());
        history.setCreatedBy(savedEmployee.getCreatedBy());
        history.setUpdatedBy(savedEmployee.getUpdatedBy());
        serviceHistoryRepository.save(history);

        // 9. Initialize Leave Balance
        initializeLeaveBalanceUseCase.execute(emp);

        // 10. Audit Log - For CREATE, use shorthand method
        auditService.log(
                AuditModule.EMPLOYEE,
                AuditAction.CREATE,
                "Employee created: " + savedEmployee.getFullName() +
                        " (" + savedEmployee.getEmployeeCode() + ")",
                savedEmployee,
                savedEmployee.getId()
        );

        // 11. Response
        DefaultResponse res = new DefaultResponse();
        res.setMsg("Employee created successfully");

        return ResponseUtils.createSuccessResponse(res, null);
    }

    // Employee Code Generator
    private String generateEmployeeCode() {
        return "EMP" + (100000 + new Random().nextInt(900000));
    }

    private String generateRandomServiceBookNo() {
        return "SB" + (100000 + new Random().nextInt(900000));
    }
}