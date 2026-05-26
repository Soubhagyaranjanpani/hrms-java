package com.hrms.employee.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeGrade;
import com.hrms.employee.dto.EmployeeUpdateReq;
import com.hrms.employee.infrastructure.EmployeeGradeRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.infrastructure.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;
    private final EmployeeGradeRepository gradeRepo;

    public ApiResponse<String> execute(Long id, EmployeeUpdateReq req) {

        log.info("Updating employee ID: {}", id);
        log.info("Request data: {}", req);

        Employee emp = employeeRepository.findById(id).orElse(null);

        if (emp == null || Boolean.TRUE.equals(emp.getIsDeleted())) {
            return ResponseUtils.createFailureResponse("Employee not found", null, "Employee not found", 404);
        }

        // Direct clean updates
        if (req.getFirstName() != null) emp.setFirstName(req.getFirstName());
        if (req.getLastName() != null) emp.setLastName(req.getLastName());
        if (req.getPhone() != null) emp.setPhone(req.getPhone());
        if (req.getAddress() != null) emp.setAddress(req.getAddress());
        if (req.getProfilePicture() != null) emp.setProfilePicture(req.getProfilePicture());
        if (req.getJoiningDate() != null) emp.setJoiningDate(req.getJoiningDate());
        if (req.getIsActive() != null) emp.setIsActive(req.getIsActive());

        // ✅ Update bank account details
        if (req.getBankAccount() != null) {
            log.info("Updating bankAccount from '{}' to '{}'", emp.getBankAccount(), req.getBankAccount());
            emp.setBankAccount(req.getBankAccount());
        }

        if (req.getUan() != null) {
            log.info("Updating uan from '{}' to '{}'", emp.getUan(), req.getUan());
            emp.setUan(req.getUan());
        }

        if (req.getPan() != null) {
            log.info("Updating pan from '{}' to '{}'", emp.getPan(), req.getPan());
            emp.setPan(req.getPan());
        }

        if (req.getGradeId() != null) {
            EmployeeGrade grade = gradeRepo.findById(req.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Grade not found with ID: " + req.getGradeId()));
            emp.setGrade(grade);
        } else {
            emp.setGrade(null);
        }

        if (req.getRoleId() != null) {
            emp.setRole(roleRepository.findById(req.getRoleId()).orElse(null));
        }

        if (req.getDepartmentId() != null) {
            emp.setDepartment(departmentRepository.findById(req.getDepartmentId()).orElse(null));
        }

        if (req.getBranchId() != null) {
            emp.setBranch(branchRepository.findById(req.getBranchId()).orElse(null));
        }

        if (req.getManagerId() != null) {
            emp.setManager(employeeRepository.findById(req.getManagerId()).orElse(null));
        }

        Employee savedEmp = employeeRepository.save(emp);

        log.info("Employee after save - bankAccount: '{}', uan: '{}', pan: '{}'",
                savedEmp.getBankAccount(), savedEmp.getUan(), savedEmp.getPan());

        return ResponseUtils.createSuccessResponse("Employee updated successfully", null);
    }
}