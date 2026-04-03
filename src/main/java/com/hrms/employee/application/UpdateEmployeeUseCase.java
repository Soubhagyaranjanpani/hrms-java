package com.hrms.employee.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.employee.dto.EmployeeUpdateReq;
import com.hrms.master.domain.Role;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.Branch;
import com.hrms.master.infrastructure.RoleRepository;
import com.hrms.master.infrastructure.DepartmentRepository;
import com.hrms.master.infrastructure.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;

    public ApiResponse<String> execute(Long id, EmployeeUpdateReq req) {

        Optional<Employee> optionalEmployee =
                employeeRepository.findByIdAndIsDeletedFalse(id);

        if (!optionalEmployee.isPresent()) {
            return ResponseUtils.createFailureResponse("Employee not found",null,"Employee not found",404);
        }

        Employee emp = optionalEmployee.get();

        // 🔥 Update only if not null (safe update)
        if (req.getFirstName() != null) emp.setFirstName(req.getFirstName());
        if (req.getLastName() != null) emp.setLastName(req.getLastName());
        if (req.getPhone() != null) emp.setPhone(req.getPhone());
        if (req.getAddress() != null) emp.setAddress(req.getAddress());
        if (req.getProfilePicture() != null) emp.setProfilePicture(req.getProfilePicture());
        if (req.getJoiningDate() != null) emp.setJoiningDate(req.getJoiningDate());
        if (req.getIsActive() != null) emp.setIsActive(req.getIsActive());

        // 🔥 Relations
        if (req.getRoleId() != null) {
            Role role = roleRepository.findById(req.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            emp.setRole(role);
        }

        if (req.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(req.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            emp.setDepartment(dept);
        }

        if (req.getBranchId() != null) {
            Branch branch = branchRepository.findById(req.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            emp.setBranch(branch);
        }

        if (req.getManagerId() != null) {
            Employee manager = employeeRepository.findById(req.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            emp.setManager(manager);
        }

        employeeRepository.save(emp);

        return ResponseUtils.createSuccessResponse("Employee updated successfully",null);
    }
}
