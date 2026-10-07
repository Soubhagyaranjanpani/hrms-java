package com.hrms.employee.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.audit.application.AuditLogService;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.security.DefaultResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.leave.application.InitializeLeaveBalanceUseCase;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.Role;
import com.hrms.master.infrastructure.BranchRepository;
import com.hrms.master.infrastructure.DepartmentRepository;
import com.hrms.master.infrastructure.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BulkUploadEmployeesUseCase {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    private final InitializeLeaveBalanceUseCase initializeLeaveBalanceUseCase;
    private final AuditLogService auditLogService;

    public ApiResponse<String> execute(MultipartFile file) {

        try {

            if (file == null || file.isEmpty()) {

                return ResponseUtils.createFailureResponse(
                        null,
                        new TypeReference<>() {},
                        "CSV file is empty",
                        400
                );
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream())
            );

            CSVParser csvParser = new CSVParser(
                    reader,
                    CSVFormat.DEFAULT
                            .builder()
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .setIgnoreHeaderCase(true)
                            .setTrim(true)
                            .build()
            );

            List<Employee> employeeList = new ArrayList<>();

            for (CSVRecord csvRecord : csvParser) {

                try {

                    String email = getString(csvRecord, "email");

                    if (email == null) {
                        continue;
                    }

                    if (employeeRepository.existsByEmail(email)) {
                        continue;
                    }

                    Employee employee = new Employee();

                    employee.setEmail(email);

                    String password = getString(csvRecord, "password");

                    if (password == null) {
                        password = "123456";
                    }

                    employee.setPassword(
                            passwordEncoder.encode(password)
                    );

                    employee.setFirstName(
                            getString(csvRecord, "first_name")
                    );

                    employee.setLastName(
                            getString(csvRecord, "last_name")
                    );

                    employee.setPhone(
                            getString(csvRecord, "phone")
                    );

                    employee.setAddress(
                            getString(csvRecord, "address")
                    );

                    /*employee.setPan(
                            getString(csvRecord, "pan")
                    );

                    employee.setUan(
                            getString(csvRecord, "uan")
                    );

                    employee.setBankAccount(
                            getString(csvRecord, "bank_account")
                    );*/

                    employee.setProfilePicture(
                            getString(csvRecord, "profile_picture")
                    );

                    employee.setJoiningDate(
                            parseDate(
                                    getString(csvRecord, "joining_date")
                            )
                    );

                    Long roleId = parseLongOrNull(
                            getString(csvRecord, "role_id")
                    );

                    if (roleId != null) {

                        Role role = roleRepository
                                .findById(roleId)
                                .orElse(null);

                        employee.setRole(role);
                    }

                    Long departmentId = parseLongOrNull(
                            getString(csvRecord, "department_id")
                    );

                    if (departmentId != null) {

                        Department department = departmentRepository
                                .findById(departmentId)
                                .orElse(null);

                        employee.setDepartment(department);
                    }

                    Long branchId = parseLongOrNull(
                            getString(csvRecord, "branch_id")
                    );

                    if (branchId != null) {

                        Branch branch = branchRepository
                                .findById(branchId)
                                .orElse(null);

                        employee.setBranch(branch);
                    }

                    employee.setEmployeeCode(
                            generateEmployeeCode()
                    );

                    employee.setIsActive(true);
                    employee.setIsDeleted(false);
                    employee.setCreatedAt(LocalDateTime.now());

                    employeeList.add(employee);

                } catch (Exception ex) {

                    System.out.println(
                            "Skipping row "
                                    + csvRecord.getRecordNumber()
                                    + " due to error: "
                                    + ex.getMessage()
                    );
                }
            }

            employeeRepository.saveAll(employeeList);

            for (Employee employee : employeeList) {

                try {
                    initializeLeaveBalanceUseCase.execute(employee);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

//            auditLogService.log(
//                    "BULK_EMPLOYEE_UPLOAD",
//                    "Bulk employee upload completed. Total employees: "
//                            + employeeList.size()
//            );

            return ResponseUtils.createSuccessResponse("Employees uploaded successfully. Total uploaded: " + employeeList.size(), new TypeReference<>() {}, "Employees uploaded successfully");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseUtils.createFailureResponse(
                    null,
                    new TypeReference<>() {},
                    e.getMessage(),
                    500
            );
        }
    }

    private String getString(CSVRecord csvRecord, String column) {

        try {

            String value = csvRecord.get(column);

            if (value == null) {
                return null;
            }

            value = value.trim();

            if (value.isEmpty()
                    || "NULL".equalsIgnoreCase(value)
                    || "null".equalsIgnoreCase(value)) {

                return null;
            }

            return value;

        } catch (Exception ex) {
            return null;
        }
    }

    private Long parseLongOrNull(String value) {

        try {

            if (value == null
                    || value.trim().isEmpty()
                    || "NULL".equalsIgnoreCase(value)
                    || "null".equalsIgnoreCase(value)) {

                return null;
            }

            return Long.parseLong(value);

        } catch (Exception ex) {
            return null;
        }
    }

    private LocalDate parseDate(String value) {

        try {

            if (value == null
                    || value.trim().isEmpty()
                    || "NULL".equalsIgnoreCase(value)
                    || "null".equalsIgnoreCase(value)) {

                return null;
            }

            return LocalDate.parse(
                    value,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
            );

        } catch (Exception ex) {
            return null;
        }
    }

    private String generateEmployeeCode() {

        String code;

        do {

            code = "EMP"
                    + (10000 + new Random().nextInt(90000));

        } while (
                employeeRepository.existsByEmployeeCode(code)
        );

        return code;
    }
}
