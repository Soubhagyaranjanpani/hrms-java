package com.hrms.employee.application;

import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.employee.dto.EmployeeProfileResponse;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.serviceBook.domain.ServiceBook;
import com.hrms.serviceBook.infrastructure.ServiceBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetAllEmployeesUseCase {

    private final EmployeeRepository employeeRepository;
    private final EmployeeDesignationRepository employeeDesignationRepository;
    private final ServiceBookRepository serviceBookRepository;


    public ApiResponse<Page<EmployeeProfileResponse>> execute(
            String name,
            Boolean isActive,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Employee> employees = employeeRepository.findAll(pageable);

        // 🔥 Apply filtering in memory (simple + consistent with your style)
        Page<EmployeeProfileResponse> response = employees.map(this::mapToResponse);

        return ResponseUtils.createSuccessResponse(response,null);
    }

    private EmployeeProfileResponse mapToResponse(Employee emp) {
        EmployeeProfileResponse res = new EmployeeProfileResponse();

        res.setId(emp.getId());
        res.setName((emp.getFirstName() + " " + emp.getLastName()).trim());
        res.setEmail(emp.getEmail());
        res.setPhone(emp.getPhone());

        if (emp.getRole() != null) res.setRoleName(emp.getRole().getName());
        if (emp.getDepartment() != null) res.setDepartmentName(emp.getDepartment().getName());
        if (emp.getBranch() != null) res.setBranchName(emp.getBranch().getName());

        res.setJoiningDate(emp.getJoiningDate());
        res.setAddress(emp.getAddress());
        res.setProfilePicture(emp.getProfilePicture());
        res.setLastLogin(emp.getLastLogin());
        // In your mapper/service where you build EmployeeProfileResponse
        if (emp.getGrade() != null) {
            res.setGradeId(emp.getGrade().getId());
            res.setGradeName(emp.getGrade().getName());
            res.setGradeCode(emp.getGrade().getCode());
            res.setGradePay(emp.getGrade().getGradePay());
        }

        res.setBankAccount(emp.getBankAccount());
        res.setUan(emp.getUan());
        res.setPan(emp.getPan());

//        Optional<EmployeeDesignation> designation =
//                employeeDesignationRepository
//                        .findFirstByEmployee_IdAndIsActiveTrueAndIsDeletedFalse(emp.getId());

//        designation.ifPresent(d ->
//                res.setDesignation(d.getDesignation().getName())
//        );
        if(emp.getDesignation()!=null){
            res.setDesignation(emp.getDesignation().getName());
        }



        Optional<ServiceBook> serviceBook =
                serviceBookRepository.findByEmployeeIdAndIsDeletedFalse(emp.getId());

        serviceBook.ifPresent(sb ->
                res.setServiceBookNo(sb.getServiceBookNo())
        );



        return res;
    }
}
