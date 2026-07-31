package com.hrms.Awards_Recognition.application;


import com.hrms.Awards_Recognition.domain.AwardRecord;
import com.hrms.Awards_Recognition.dto.CreateAwardRequest;
import com.hrms.Awards_Recognition.dto.AwardRecordResponse;
import com.hrms.Awards_Recognition.infrastructure.AwardRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.AwardType;
import com.hrms.master.infrastructure.AwardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateAwardUseCase {

    private final AwardRepository awardRepo;
    private final EmployeeRepository empRepo;
    private final AwardTypeRepository awardTypeRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final AwardMapper mapper;
    private final PdfAwardCertificateGenerator certificateGenerator;
    private final AwardDocumentStorageService storageService;

    @Transactional
    public AwardRecordResponse execute(CreateAwardRequest req) {
        // 1. Fetch Employee
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + req.getEmployeeId()));

        // 2. ✅ Fetch Award Type - Dropdown 1
        AwardType awardType = null;
        if (req.getAwardTypeId() != null) {
            awardType = awardTypeRepo.findById(req.getAwardTypeId())
                    .orElseThrow(() -> new RuntimeException("Award Type not found with ID: " + req.getAwardTypeId()));
        }

        // 3. ✅ Fetch Issued By - Dropdown 2
        EmployeeDesignation issuedBy = null;
        if (req.getIssuedById() != null) {
            issuedBy = employeeDesignationRepo.findById(req.getIssuedById())
                    .orElseThrow(() -> new RuntimeException("Issued By not found with ID: " + req.getIssuedById()));
        }

        // 4. Create Award Record
        AwardRecord a = new AwardRecord();
        a.setEmployee(emp);
        a.setAwardName(req.getAwardName());
        a.setAwardDate(req.getAwardDate() != null ? req.getAwardDate() : LocalDate.now());
        a.setAwardType(awardType);
        a.setIssuedBy(issuedBy);
        a.setDescription(req.getDescription());

        // Auto-populate department/designation from employee
        a.setDepartmentName(emp.getDepartment() != null ? emp.getDepartment().getName() : null);

        employeeDesignationRepo.findFirstByEmployee_IdAndIsActiveTrueAndIsDeletedFalse(emp.getId())
                .ifPresent(currentAssignment -> {
                    if (currentAssignment.getDesignation() != null) {
                        a.setDesignationName(currentAssignment.getDesignation().getName());
                    }
                });

        a.setIsActive(true);

        // 5. Save
        AwardRecord saved = awardRepo.save(a);

        // 6. Auto-generate award certificate
        try {
            byte[] pdfBytes = certificateGenerator.generateCertificate(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = awardRepo.save(saved);
        } catch (Exception e) {
            System.err.println("Failed to auto-generate award certificate: " + e.getMessage());
        }

        return mapper.toResponse(saved);
    }
}