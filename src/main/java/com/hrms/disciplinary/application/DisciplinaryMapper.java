package com.hrms.disciplinary.application;

import com.hrms.disciplinary.domain.DisciplinaryRecord;
import com.hrms.disciplinary.dto.DisciplinaryRecordResponse;
import com.hrms.master.dto.ActionTypeResponse;
import com.hrms.master.dto.PenaltyTypeResponse;
import org.springframework.stereotype.Component;

@Component
public class DisciplinaryMapper {

    public DisciplinaryRecordResponse toResponse(DisciplinaryRecord d) {
        DisciplinaryRecordResponse r = new DisciplinaryRecordResponse();
        r.setId(d.getId());
        r.setEmployeeId(d.getEmployee().getId());

        // ✅ Fix: Use firstName + lastName instead of fullName
        String fullName = (d.getEmployee().getFirstName() != null ? d.getEmployee().getFirstName() : "") +
                " " + (d.getEmployee().getLastName() != null ? d.getEmployee().getLastName() : "");
        r.setEmployee(fullName.trim());

        r.setEmployeeCode(d.getEmployee().getEmployeeCode());

        r.setDepartment(d.getDepartmentName() != null ? d.getDepartmentName() : "—");
        r.setDesignation(d.getDesignationName() != null ? d.getDesignationName() : "—");

        r.setCaseNumber(d.getCaseNumber());
        r.setIncidentDate(d.getIncidentDate());
        r.setResolutionDate(d.getResolutionDate());
        r.setRemarks(d.getRemarks());

        // ✅ Map Action Type - Dropdown 1
        if (d.getActionType() != null) {
            ActionTypeResponse actionResponse = new ActionTypeResponse();
            actionResponse.setId(d.getActionType().getId());
            actionResponse.setName(d.getActionType().getName());
            actionResponse.setIsActive(d.getActionType().getIsActive());
            r.setActionType(actionResponse);
        }

        // ✅ Map Investigation Officer - Dropdown 2
        if (d.getInvestigationOfficer() != null) {
            r.setInvestigationOfficerId(d.getInvestigationOfficer().getId());
            if (d.getInvestigationOfficer().getEmployee() != null) {
                String officerName = (d.getInvestigationOfficer().getEmployee().getFirstName() != null ?
                        d.getInvestigationOfficer().getEmployee().getFirstName() : "") +
                        " " + (d.getInvestigationOfficer().getEmployee().getLastName() != null ?
                        d.getInvestigationOfficer().getEmployee().getLastName() : "");
                r.setInvestigationOfficerName(officerName.trim());
            }
            if (d.getInvestigationOfficer().getDesignation() != null) {
                r.setInvestigationOfficerDesignation(d.getInvestigationOfficer().getDesignation().getName());
            }
        }

        // ✅ Map Penalty Type - Dropdown 3
        if (d.getPenaltyType() != null) {
            PenaltyTypeResponse penaltyResponse = new PenaltyTypeResponse();
            penaltyResponse.setId(d.getPenaltyType().getId());
            penaltyResponse.setName(d.getPenaltyType().getName());
            penaltyResponse.setIsActive(d.getPenaltyType().getIsActive());
            r.setPenaltyType(penaltyResponse);
        }

        r.setIsActive(d.getIsActive());
        r.setDocumentPath(d.getDocumentPath());
        r.setDocumentName(d.getDocumentName());
        r.setProcessedBy(d.getProcessedBy());
        r.setCreatedAt(d.getCreatedAt());
        r.setUpdatedAt(d.getUpdatedAt());

        return r;
    }
}