package com.hrms.employee.application;

import aj.org.objectweb.asm.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.dto.EmployeeProfileResponse;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllEmployeesUseCase {

    private final EmployeeRepository employeeRepository;

    public ApiResponse<Page<EmployeeProfileResponse>> execute(
            String name,
            Boolean isActive,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Employee> employeePage;

        if (name != null && isActive != null) {
            employeePage = employeeRepository
                    .findByFirstNameContainingIgnoreCaseAndIsActiveAndIsDeletedFalse(
                            name, isActive, pageable);
        } else if (name != null) {
            employeePage = employeeRepository
                    .findByFirstNameContainingIgnoreCaseAndIsDeletedFalse(
                            name, pageable);
        } else if (isActive != null) {
            employeePage = employeeRepository
                    .findByIsActiveAndIsDeletedFalse(
                            isActive, pageable);
        } else {
            employeePage = employeeRepository
                    .findByIsDeletedFalse(pageable);
        }

        Page<EmployeeProfileResponse> responsePage =
                employeePage.map(this::mapToResponse);

        // 🔥 FIXED: according to your ResponseUtils
        return ResponseUtils.createSuccessResponse(responsePage, null);
    }

    private EmployeeProfileResponse mapToResponse(Employee emp) {
        EmployeeProfileResponse res = new EmployeeProfileResponse();

        res.setId(emp.getId());

        // ✅ Combine first + last name
        res.setName(
                (emp.getFirstName() != null ? emp.getFirstName() : "") +
                        " " +
                        (emp.getLastName() != null ? emp.getLastName() : "")
        );

        res.setEmail(emp.getEmail());
        res.setPhone(emp.getPhone());

        if (emp.getRole() != null) {
            res.setRoleName(emp.getRole().getName());
        }

        if (emp.getDepartment() != null) {
            res.setDepartmentName(emp.getDepartment().getName());
        }

        if (emp.getBranch() != null) {
            res.setBranchName(emp.getBranch().getName());
        }

        res.setJoiningDate(emp.getJoiningDate());
        res.setAddress(emp.getAddress());
        res.setProfilePicture(emp.getProfilePicture());
        res.setLastLogin(emp.getLastLogin());

        return res;
    }
}
