package com.hrms.training.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.training.domain.TrainingRecord;
import com.hrms.training.dto.CreateTrainingRequest;
import com.hrms.training.dto.TrainingItemRequest;
import com.hrms.training.dto.TrainingRecordResponse;
import com.hrms.training.infrastructure.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateTrainingUseCase {

    private final TrainingRepository trainingRepo;
    private final EmployeeRepository empRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final TrainingMapper mapper;
    private final PdfTrainingCertificateGenerator certificateGenerator;
    private final TrainingDocumentStorageService storageService;
    private final AuditService auditService;

    /** Saves one or more training rows in a single call — mirrors the "+ add row / Save N Training(s)" form. */
    public List<TrainingRecordResponse> execute(CreateTrainingRequest req) {
        if (req.getTrainings() == null || req.getTrainings().isEmpty()) {
            throw new RuntimeException("At least one training row is required");
        }

        List<TrainingRecordResponse> responses = new ArrayList<>();

        for (TrainingItemRequest item : req.getTrainings()) {
            Employee emp = empRepo.findById(item.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found: " + item.getEmployeeId()));

            TrainingRecord r = new TrainingRecord();
            r.setEmployee(emp);
            r.setTrainingName(item.getTrainingName());
            r.setProvider(item.getProvider());
            r.setStartDate(item.getStartDate());
            r.setEndDate(item.getEndDate());
            r.setHours(item.getHours());
            r.setCertification(item.getCertification());
            r.setIsActive(true);

            // ── Auto-populate department/designation from the employee's current record ──
            r.setDepartmentName(emp.getDepartment() != null ? emp.getDepartment().getName() : null);
            employeeDesignationRepo.findFirstByEmployee_IdAndIsActiveTrueAndIsDeletedFalse(emp.getId())
                    .ifPresent(currentAssignment -> {
                        if (currentAssignment.getDesignation() != null) {
                            r.setDesignationName(currentAssignment.getDesignation().getName());
                        }
                    });

            TrainingRecord saved = trainingRepo.save(r);

            // Auto-generate the training certificate and persist its path/name on the record
            boolean certificateGenerated = false;
            try {
                byte[] pdfBytes = certificateGenerator.generateCertificate(saved);
                String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

                saved.setDocumentPath(path);
                saved.setDocumentName(storageService.fileNameOf(path));
                saved = trainingRepo.save(saved);
                certificateGenerated = true;
            } catch (Exception e) {
                System.err.println("Failed to auto-generate training certificate for id " + saved.getId() + ": " + e.getMessage());
            }

            // ── AUDIT LOGGING ──

            // 1. Main training record creation audit
            auditService.log(
                    AuditModule.TRAINING,
                    AuditAction.CREATE,
                    "Training record created for " + emp.getFullName() +
                            " (" + emp.getEmployeeCode() + ")",
                    emp,
                    "Training Record",
                    null,
                    "Training: " + saved.getTrainingName() +
                            ", Provider: " + (saved.getProvider() != null ? saved.getProvider() : "N/A"),
                    "Training from " + saved.getStartDate() +
                            " to " + (saved.getEndDate() != null ? saved.getEndDate().toString() : "N/A") +
                            ", Hours: " + (saved.getHours() != null ? saved.getHours() : "N/A") +
                            (saved.getCertification() != null ? ", Certification: " + saved.getCertification() : ""),
                    saved.getId()
            );

            // 2. Certificate generation audit (if successful)
            if (certificateGenerated) {
                auditService.log(
                        AuditModule.DOCUMENTS,
                        AuditAction.UPLOAD,
                        "Training certificate generated: " + saved.getDocumentName(),
                        emp,
                        "Document",
                        null,
                        saved.getDocumentName(),
                        "Auto-generated training certificate",
                        saved.getId()
                );
            }

            responses.add(mapper.toResponse(saved));
        }

        // 3. Batch audit log for multiple trainings
        if (req.getTrainings().size() > 1) {
            auditService.log(
                    AuditModule.TRAINING,
                    AuditAction.CREATE,
                    "Batch training creation completed",
                    null,
                    "Batch Training",
                    null,
                    req.getTrainings().size() + " trainings",
                    "Created " + req.getTrainings().size() + " training records in batch",
                    null
            );
        }

        return responses;
    }
}