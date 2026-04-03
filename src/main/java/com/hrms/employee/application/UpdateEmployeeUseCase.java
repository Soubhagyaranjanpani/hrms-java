package com.hrms.employee.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.dto.EmployeeUpdateReq;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.infrastructure.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;

    public ApiResponse<String> execute(Long id, EmployeeUpdateReq req) {

        Employee emp = employeeRepository.findById(id).orElse(null);

        if (emp == null || Boolean.TRUE.equals(emp.getIsDeleted())) {
            return ResponseUtils.createFailureResponse("Employee not found",null,"Employee not found",404);
        }

        // 🔥 Direct clean updates
        if (req.getFirstName() != null) emp.setFirstName(req.getFirstName());
        if (req.getLastName() != null) emp.setLastName(req.getLastName());
        if (req.getPhone() != null) emp.setPhone(req.getPhone());
        if (req.getAddress() != null) emp.setAddress(req.getAddress());
        if (req.getProfilePicture() != null) emp.setProfilePicture(req.getProfilePicture());
        if (req.getJoiningDate() != null) emp.setJoiningDate(req.getJoiningDate());
        if (req.getIsActive() != null) emp.setIsActive(req.getIsActive());

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

        employeeRepository.save(emp);

        return ResponseUtils.createSuccessResponse("Employee updated successfully",null);
    }
}
