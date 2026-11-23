
package com.hrms.EmployeeVerification.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrms.EmployeeVerification.domain.EmployeeVerificationRequest;
import com.hrms.EmployeeVerification.domain.VerificationStatus;
import com.hrms.EmployeeVerification.dto.SubmitVerificationDto;
import com.hrms.EmployeeVerification.infrastructure.EmployeeVerificationRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyVerificationUseCase {

    private final EmployeeVerificationRepository verificationRepo;
    private final EmployeeRepository employeeRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long execute(String email, SubmitVerificationDto dto) {

        // STEP 1: Check authenticated employee
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Authenticated employee email is required");
        }

        Employee employee = employeeRepo
                .findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found: " + email));

        log.info("Verification submission for employee ID: {}",
                employee.getId());

        // STEP 2: Validate form
        if (dto == null) {
            throw new IllegalArgumentException(
                    "Verification form is required");
        }

        if (dto.getClientName() == null ||
                dto.getClientName().isBlank()) {
            throw new IllegalArgumentException(
                    "Client name is required");
        }

        if (dto.getSiteLocation() == null ||
                dto.getSiteLocation().isBlank()) {
            throw new IllegalArgumentException(
                    "Site location is required");
        }

        if (dto.getPurposeDescription() == null ||
                dto.getPurposeDescription().isBlank()) {
            throw new IllegalArgumentException(
                    "Purpose description is required");
        }

        if (dto.getDates() == null ||
                dto.getDates().isEmpty()) {
            throw new IllegalArgumentException(
                    "Select at least one work date");
        }

        if (dto.getDates().contains(null)) {
            throw new IllegalArgumentException(
                    "Work dates cannot contain null");
        }

        if (new HashSet<>(dto.getDates()).size()
                != dto.getDates().size()) {
            throw new IllegalArgumentException(
                    "Duplicate work dates are not allowed");
        }

        // STEP 3: Convert dates to JSON
        String datesJson;

        try {
            datesJson = objectMapper.writeValueAsString(
                    dto.getDates());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to serialize work dates", e);
        }

        // STEP 4: Prepare database entity
        EmployeeVerificationRequest request =
                new EmployeeVerificationRequest();

        request.setEmployee(employee);
        request.setClientName(dto.getClientName());
        request.setProjectName(dto.getProjectName());
        request.setSiteLocation(dto.getSiteLocation());
        request.setContactPerson(dto.getContactPerson());
        request.setPurposeDescription(
                dto.getPurposeDescription());

        request.setDatesJson(datesJson);
        request.setTotalDays(dto.getDates().size());

        request.setStatus(
                VerificationStatus.PENDING_VERIFICATION);

        request.setCreatedAt(LocalDateTime.now());
        request.setIsDeleted(false);

        // STEP 5: Save and flush to PostgreSQL
        EmployeeVerificationRequest saved =
                verificationRepo.saveAndFlush(request);

        log.info("Verification flushed to database. Request ID: {}",
                saved.getId());

        return saved.getId();
    }
}
