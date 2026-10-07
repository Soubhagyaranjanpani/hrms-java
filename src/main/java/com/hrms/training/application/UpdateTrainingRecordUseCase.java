package com.hrms.training.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.training.domain.TrainingRecord;
import com.hrms.training.dto.TrainingRecordResponse;
import com.hrms.training.dto.UpdateTrainingRequest;
import com.hrms.training.infrastructure.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateTrainingRecordUseCase {

    private final TrainingRepository repo;
    private final TrainingMapper mapper;
    private final AuditService auditService;

    public TrainingRecordResponse execute(Long id, UpdateTrainingRequest req) {
        TrainingRecord t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Training record not found"));

        // Capture old values for audit
        String oldTrainingName = t.getTrainingName();
        String oldProvider = t.getProvider();
        Integer oldHours = t.getHours();
        String oldCertification = t.getCertification();

        // Update fields
        if (req.getTrainingName() != null) t.setTrainingName(req.getTrainingName());
        if (req.getProvider() != null) t.setProvider(req.getProvider());
        if (req.getStartDate() != null) t.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) t.setEndDate(req.getEndDate());
        if (req.getHours() != null) t.setHours(req.getHours());
        if (req.getCertification() != null) t.setCertification(req.getCertification());

        TrainingRecord updatedTraining = repo.save(t);

        // Build change summary
        List<String> changes = new ArrayList<>();

        if (oldTrainingName != null && !oldTrainingName.equals(updatedTraining.getTrainingName())) {
            changes.add("Training Name: " + oldTrainingName + " → " +
                    updatedTraining.getTrainingName());
        }

        if (oldProvider != null && !oldProvider.equals(updatedTraining.getProvider())) {
            changes.add("Provider: " + oldProvider + " → " +
                    updatedTraining.getProvider());
        }

        if (req.getStartDate() != null) {
            changes.add("Start Date updated");
        }

        if (req.getEndDate() != null) {
            changes.add("End Date updated");
        }

        if (oldHours != null && !oldHours.equals(updatedTraining.getHours())) {
            changes.add("Hours: " + oldHours + " → " + updatedTraining.getHours());
        }

        if (oldCertification != null && !oldCertification.equals(updatedTraining.getCertification())) {
            changes.add("Certification updated");
        }

        // Audit Log - Single consolidated entry
        if (!changes.isEmpty()) {
            auditService.log(
                    AuditModule.TRAINING,
                    AuditAction.UPDATE,
                    "Training record updated for " +
                            (updatedTraining.getEmployee() != null ?
                                    updatedTraining.getEmployee().getFullName() +
                                    " (" + updatedTraining.getEmployee().getEmployeeCode() + ")" :
                                    "record"),
                    updatedTraining.getEmployee(),
                    "Multiple Fields",
                    oldTrainingName,
                    updatedTraining.getTrainingName(),
                    String.join("; ", changes),
                    updatedTraining.getId()
            );
        }

        return mapper.toResponse(updatedTraining);
    }
}