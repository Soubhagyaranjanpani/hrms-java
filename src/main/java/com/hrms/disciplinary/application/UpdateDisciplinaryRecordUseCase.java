package com.hrms.disciplinary.application;

import com.hrms.disciplinary.domain.DisciplinaryRecord;
import com.hrms.disciplinary.dto.DisciplinaryRecordResponse;
import com.hrms.disciplinary.dto.UpdateDisciplinaryRequest;
import com.hrms.disciplinary.infrastructure.DisciplinaryRepository;
import com.hrms.employee.domain.EmployeeDesignation;  // ✅ ADD THIS
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.master.domain.ActionType;  // ✅ ADD THIS
import com.hrms.master.domain.PenaltyType;  // ✅ ADD THIS
import com.hrms.master.infrastructure.ActionTypeRepository;
import com.hrms.master.infrastructure.PenaltyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDisciplinaryRecordUseCase {

    private final DisciplinaryRepository repo;
    private final DisciplinaryMapper mapper;
    private final ActionTypeRepository actionTypeRepo;
    private final PenaltyTypeRepository penaltyTypeRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;

    @Transactional
    public DisciplinaryRecordResponse execute(Long id, UpdateDisciplinaryRequest req) {
        DisciplinaryRecord d = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplinary record not found with ID: " + id));

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

        // ✅ Update Dropdown 1 - Action Type
        if (req.getActionTypeId() != null) {
            ActionType actionType = actionTypeRepo.findById(req.getActionTypeId())
                    .orElseThrow(() -> new RuntimeException("Action Type not found with ID: " + req.getActionTypeId()));
            d.setActionType(actionType);
        }

        // ✅ Update Dropdown 3 - Penalty Type
        if (req.getPenaltyTypeId() != null) {
            PenaltyType penaltyType = penaltyTypeRepo.findById(req.getPenaltyTypeId())
                    .orElseThrow(() -> new RuntimeException("Penalty Type not found with ID: " + req.getPenaltyTypeId()));
            d.setPenaltyType(penaltyType);
        }

        // ✅ Update Dropdown 2 - Investigation Officer
        if (req.getInvestigationOfficerId() != null) {
            EmployeeDesignation officer = employeeDesignationRepo.findById(req.getInvestigationOfficerId())
                    .orElseThrow(() -> new RuntimeException("Investigation Officer not found with ID: " + req.getInvestigationOfficerId()));
            d.setInvestigationOfficer(officer);
        }

        return mapper.toResponse(repo.save(d));
    }
}