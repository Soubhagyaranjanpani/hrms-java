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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * NOTE: This service throws {@link EmployeeDesignationNotFoundException} /
 * {@link RelatedEntityNotFoundException} instead of plain RuntimeException so that
 * a @ControllerAdvice can map them to proper HTTP status codes (404 vs 400/409).
 */
@Service
@RequiredArgsConstructor
public class EmployeeDesignationService {

    private final EmployeeDesignationRepository employeeDesignationRepository;
    private final EmployeeRepository employeeRepository;
    private final DesignationRepository designationRepository;

    @Transactional
    public EmployeeDesignationResponse create(EmployeeDesignationRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RelatedEntityNotFoundException(
                        "Employee not found with id: " + request.getEmployeeId()));

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new RelatedEntityNotFoundException(
                        "Designation not found with id: " + request.getDesignationId()));

        employeeDesignationRepository
                .findFirstByEmployee_IdAndIsActiveTrueAndIsDeletedFalse(request.getEmployeeId())
                .ifPresent(existing -> {
                    throw new DuplicateActiveDesignationException(
                            "Employee " + request.getEmployeeId() + " already has an active designation");
                });

        EmployeeDesignation entity = new EmployeeDesignation();
        entity.setEmployee(employee);
        entity.setDesignation(designation);
        entity.setCreatedDate(LocalDate.now());
        entity.setIsActive(true);
        entity.setIsDeleted(false);

        entity = employeeDesignationRepository.save(entity);
        return mapToResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDesignationResponse> getAll(Integer flag) {

        boolean activeOnly = Objects.equals(flag, 1);

        List<EmployeeDesignation> list = activeOnly
                ? employeeDesignationRepository.findByIsActiveTrueAndIsDeletedFalse()
                : employeeDesignationRepository.findByIsDeletedFalse();

        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeDesignationResponse getById(Long id) {
        EmployeeDesignation entity = findActiveOrThrow(id);
        return mapToResponse(entity);
    }

    @Transactional
    public EmployeeDesignationResponse update(Long id, EmployeeDesignationRequest request) {

        EmployeeDesignation entity = findActiveOrThrow(id);

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RelatedEntityNotFoundException(
                        "Employee not found with id: " + request.getEmployeeId()));

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new RelatedEntityNotFoundException(
                        "Designation not found with id: " + request.getDesignationId()));

        entity.setEmployee(employee);
        entity.setDesignation(designation);
        entity.setUpdatedDate(LocalDate.now());

        entity = employeeDesignationRepository.save(entity);
        return mapToResponse(entity);
    }

    /**
     * Sets the active/inactive status explicitly based on the given value.
     */
    @Transactional
    public void changeStatus(Long id, Boolean active) {
        EmployeeDesignation entity = findActiveOrThrow(id);
        entity.setIsActive(active);
        entity.setUpdatedDate(LocalDate.now());
        employeeDesignationRepository.save(entity);
    }

    @Transactional
    public void delete(Long id) {

        EmployeeDesignation entity = employeeDesignationRepository.findById(id)
                .orElseThrow(() -> new EmployeeDesignationNotFoundException(
                        "Record not found with id: " + id));

        if (Boolean.TRUE.equals(entity.getIsDeleted())) {
            return;
        }

        entity.setIsDeleted(true);
        employeeDesignationRepository.save(entity);
    }

    private EmployeeDesignation findActiveOrThrow(Long id) {
        EmployeeDesignation entity = employeeDesignationRepository.findById(id)
                .orElseThrow(() -> new EmployeeDesignationNotFoundException(
                        "Record not found with id: " + id));

        if (Boolean.TRUE.equals(entity.getIsDeleted())) {
            throw new EmployeeDesignationNotFoundException(
                    "Record has been deleted with id: " + id);
        }
        return entity;
    }

    private EmployeeDesignationResponse mapToResponse(EmployeeDesignation entity) {

        String firstName = entity.getEmployee().getFirstName() != null
                ? entity.getEmployee().getFirstName() : "";
        String lastName = entity.getEmployee().getLastName() != null
                ? entity.getEmployee().getLastName() : "";

        EmployeeDesignationResponse response = new EmployeeDesignationResponse();
        response.setId(entity.getId());
        response.setEmployeeName((firstName + " " + lastName).trim());
        response.setDesignationName(entity.getDesignation().getName());
        response.setCreatedDate(entity.getCreatedDate());
        response.setUpdatedDate(entity.getUpdatedDate());
        response.setIsActive(entity.getIsActive());
        response.setIsDeleted(entity.getIsDeleted());
        return response;
    }

    public static class DuplicateActiveDesignationException extends RuntimeException {
        public DuplicateActiveDesignationException(String message) {
            super(message);
        }
    }

    public static class EmployeeDesignationNotFoundException extends RuntimeException {
        public EmployeeDesignationNotFoundException(String message) {
            super(message);
        }
    }

    public static class RelatedEntityNotFoundException extends RuntimeException {
        public RelatedEntityNotFoundException(String message) {
            super(message);
        }
    }
}