package com.hrms.deputation.application;

import com.hrms.deputation.domain.DeputationRecord;
import com.hrms.deputation.dto.CreateDeputationRequest;
import com.hrms.deputation.dto.DeputationRecordResponse;
import com.hrms.deputation.infrastructure.DeputationRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.DeputationType;
import com.hrms.master.infrastructure.DeputationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateDeputationUseCase {

    private final DeputationRepository deputationRepo;
    private final EmployeeRepository empRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final DeputationTypeRepository deputationTypeRepo;
    private final DeputationMapper mapper;
    private final PdfDeputationLetterGenerator letterGenerator;
    private final DeputationDocumentStorageService storageService;

    public DeputationRecordResponse execute(CreateDeputationRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Fetch Deputation Type
        DeputationType deputationType = null;
        if (req.getDeputationTypeId() != null) {
            deputationType = deputationTypeRepo.findById(req.getDeputationTypeId())
                    .orElseThrow(() -> new RuntimeException("Deputation Type not found"));
        }

        // Fetch Reporting Authority
        EmployeeDesignation authority = employeeDesignationRepo.findById(req.getReportingAuthorityId())
                .orElseThrow(() -> new RuntimeException("Reporting authority not found"));

        DeputationRecord r = new DeputationRecord();
        r.setEmployee(emp);
        r.setDeputationOrderNumber(req.getDeputationOrderNumber());
        r.setDeputationOrganization(req.getDeputationOrganization());
        r.setDeputationType(deputationType);
        r.setReportingAuthority(authority);
        r.setStartDate(req.getStartDate() != null ? req.getStartDate() : LocalDate.now());
        r.setEndDate(req.getEndDate());
        r.setRemarks(req.getRemarks());

        // Auto-populate department/designation from employee
        r.setDepartmentName(emp.getDepartment() != null ? emp.getDepartment().getName() : null);

        employeeDesignationRepo.findFirstByEmployee_IdAndIsActiveTrueAndIsDeletedFalse(emp.getId())
                .ifPresent(currentAssignment -> {
                    if (currentAssignment.getDesignation() != null) {
                        r.setDesignationName(currentAssignment.getDesignation().getName());
                    }
                });

        r.setIsActive(true);

        DeputationRecord saved = deputationRepo.save(r);

        // Auto-generate deputation letter
        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = deputationRepo.save(saved);
        } catch (Exception e) {
            System.err.println("Failed to auto-generate deputation letter: " + e.getMessage());
        }

        return mapper.toResponse(saved);
    }
}