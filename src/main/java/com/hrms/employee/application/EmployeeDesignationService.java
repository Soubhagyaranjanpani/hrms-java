package com.hrms.employee.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.employee.dto.EmployeeDesignationRequest;
import com.hrms.employee.dto.EmployeeDesignationResponse;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.Designation;
import com.hrms.master.infrastructure.DesignationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeDesignationService {

    private final EmployeeDesignationRepository employeeDesignationRepository;
    private final EmployeeRepository employeeRepository;
    private final DesignationRepository designationRepository;

    public EmployeeDesignationResponse create(EmployeeDesignationRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        EmployeeDesignation entity = new EmployeeDesignation();
        entity.setEmployee(employee);
        entity.setDesignation(designation);
        entity.setCreatedDate(LocalDate.now());
        // isActive and isDeleted are already defaulted to true/false

        entity = employeeDesignationRepository.save(entity);
        return mapToResponse(entity);
    }

    // ✅ Modified to accept flag
    public List<EmployeeDesignationResponse> getAll(Integer flag) {

        List<EmployeeDesignation> list;

        if (flag == 1) {
            // only active (and not deleted)
            list = employeeDesignationRepository.findByIsActiveTrueAndIsDeletedFalse();
        } else {
            // all non‑deleted (flag = 0 or any other value)
            list = employeeDesignationRepository.findByIsDeletedFalse();
        }

        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EmployeeDesignationResponse getById(Long id) {

        EmployeeDesignation entity = employeeDesignationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        // Optional: check if deleted? up to you
        return mapToResponse(entity);
    }

    public EmployeeDesignationResponse update(Long id, EmployeeDesignationRequest request) {

        EmployeeDesignation entity = employeeDesignationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        entity.setEmployee(employee);
        entity.setDesignation(designation);

        entity = employeeDesignationRepository.save(entity);
        return mapToResponse(entity);
    }

    // ✅ Soft delete: set isDeleted = true
    public void delete(Long id) {

        EmployeeDesignation entity = employeeDesignationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        entity.setIsDeleted(true);
        employeeDesignationRepository.save(entity);
    }

    private EmployeeDesignationResponse mapToResponse(EmployeeDesignation entity) {

        EmployeeDesignationResponse response = new EmployeeDesignationResponse();
        response.setId(entity.getId());
        response.setEmployeeName(
                entity.getEmployee().getFirstName() + " " +
                        entity.getEmployee().getLastName()
        );
        response.setDesignationName(entity.getDesignation().getName());
        response.setCreatedDate(entity.getCreatedDate());
        // you can also set isActive if needed in response
        return response;
    }
}