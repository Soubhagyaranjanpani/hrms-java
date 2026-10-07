package com.hrms.disciplinary.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.disciplinary.domain.DisciplinaryRecord;
import com.hrms.disciplinary.dto.DisciplinaryRecordResponse;
import com.hrms.disciplinary.dto.UpdateDisciplinaryRequest;
import com.hrms.disciplinary.infrastructure.DisciplinaryRepository;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.master.domain.ActionType;
import com.hrms.master.domain.PenaltyType;
import com.hrms.master.infrastructure.ActionTypeRepository;
import com.hrms.master.infrastructure.PenaltyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateDisciplinaryRecordUseCase {

    private final DisciplinaryRepository repo;
    private final DisciplinaryMapper mapper;
    private final ActionTypeRepository actionTypeRepo;
    private final PenaltyTypeRepository penaltyTypeRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final AuditService auditService;

    @Transactional
    public DisciplinaryRecordResponse execute(Long id, UpdateDisciplinaryRequest req) {
        DisciplinaryRecord d = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplinary record not found with ID: " + id));

        // Capture old values for audit
        String oldCaseNumber = d.getCaseNumber();
        String oldActionType = d.getActionType() != null ? d.getActionType().toString() : null;
        String oldPenaltyType = d.getPenaltyType() != null ? d.getPenaltyType().toString() : null;
        String oldInvestigationOfficer = d.getInvestigationOfficer() != null ?
                d.getInvestigationOfficer().toString() : null;
        String oldRemarks = d.getRemarks();

        // Update fields
        if (req.getCaseNumber() != null) {
            d.setCaseNumber(req.getCaseNumber());
        }
        if (req.getIncidentDate() != null) {
            d.setIncidentDate(req.getIncidentDate());
        }
        if (req.getResolutionDate() != null) {
            d.setResolutionDate(req.getResolutionDate());
        }
        if (req.getRemarks() != null) {
            d.setRemarks(req.getRemarks());
        }

        // Update Action Type
        if (req.getActionTypeId() != null) {
            ActionType actionType = actionTypeRepo.findById(req.getActionTypeId())
                    .orElseThrow(() -> new RuntimeException("Action Type not found with ID: " + req.getActionTypeId()));
            d.setActionType(actionType);
        }

        // Update Penalty Type
        if (req.getPenaltyTypeId() != null) {
            PenaltyType penaltyType = penaltyTypeRepo.findById(req.getPenaltyTypeId())
                    .orElseThrow(() -> new RuntimeException("Penalty Type not found with ID: " + req.getPenaltyTypeId()));
            d.setPenaltyType(penaltyType);
        }

        // Update Investigation Officer
        if (req.getInvestigationOfficerId() != null) {
            EmployeeDesignation officer = employeeDesignationRepo.findById(req.getInvestigationOfficerId())
                    .orElseThrow(() -> new RuntimeException("Investigation Officer not found with ID: " + req.getInvestigationOfficerId()));
            d.setInvestigationOfficer(officer);
        }

        DisciplinaryRecord updatedDisciplinary = repo.save(d);

        // Build change summary
        List<String> changes = new ArrayList<>();

        if (oldCaseNumber != null && !oldCaseNumber.equals(updatedDisciplinary.getCaseNumber())) {
            changes.add("Case Number: " + oldCaseNumber + " → " +
                    updatedDisciplinary.getCaseNumber());
        }

        if (req.getIncidentDate() != null) {
            changes.add("Incident Date updated");
        }

        if (req.getResolutionDate() != null) {
            changes.add("Resolution Date updated");
        }

        String newActionType = updatedDisciplinary.getActionType() != null ?
                updatedDisciplinary.getActionType().toString() : null;
        if (oldActionType != null && !oldActionType.equals(newActionType)) {
            changes.add("Action Type: " + oldActionType + " → " + newActionType);
        }

        String newPenaltyType = updatedDisciplinary.getPenaltyType() != null ?
                updatedDisciplinary.getPenaltyType().toString() : null;
        if (oldPenaltyType != null && !oldPenaltyType.equals(newPenaltyType)) {
            changes.add("Penalty Type: " + oldPenaltyType + " → " + newPenaltyType);
        }

        String newInvestigationOfficer = updatedDisciplinary.getInvestigationOfficer() != null ?
                updatedDisciplinary.getInvestigationOfficer().toString() : null;
        if (oldInvestigationOfficer != null && !oldInvestigationOfficer.equals(newInvestigationOfficer)) {
            changes.add("Officer: " + oldInvestigationOfficer + " → " + newInvestigationOfficer);
        }

        if (oldRemarks != null && !oldRemarks.equals(updatedDisciplinary.getRemarks())) {
            changes.add("Remarks updated");
        }

        // Audit Log - Single consolidated entry
        if (!changes.isEmpty()) {
            auditService.log(
                    AuditModule.DISCIPLINARY,
                    AuditAction.UPDATE,
                    "Disciplinary record updated for " +
                            (updatedDisciplinary.getEmployee() != null ?
                                    updatedDisciplinary.getEmployee().getFullName() +
                                    " (" + updatedDisciplinary.getEmployee().getEmployeeCode() + ")" :
                                    "record"),
                    updatedDisciplinary.getEmployee(),
                    "Multiple Fields",
                    oldCaseNumber,
                    updatedDisciplinary.getCaseNumber(),
                    String.join("; ", changes),
                    updatedDisciplinary.getId()
            );
        }

        return mapper.toResponse(updatedDisciplinary);
    }
}