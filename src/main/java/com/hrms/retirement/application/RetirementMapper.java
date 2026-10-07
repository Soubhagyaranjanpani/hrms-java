package com.hrms.retirement.application;

import com.hrms.retirement.domain.RetirementRecord;
import com.hrms.retirement.dto.RetirementRecordResponse;
import org.springframework.stereotype.Component;

@Component
public class RetirementMapper {

    public RetirementRecordResponse toResponse(RetirementRecord d) {
        RetirementRecordResponse r = new RetirementRecordResponse();
        r.setId(d.getId());
        r.setEmployeeId(d.getEmployee().getId());

        r.setEmployee(d.getEmployee().getFullName());
        r.setEmployeeCode(d.getEmployee().getEmployeeCode());

        r.setDepartment(d.getDepartmentName() != null ? d.getDepartmentName() : "—");
        r.setDesignation(d.getDesignationName() != null ? d.getDesignationName() : "—");

        r.setRetirementDate(d.getRetirementDate());

        if (d.getRetirementType() != null) {
            r.setRetirementTypeId(d.getRetirementType().getId());
            r.setRetirementType(d.getRetirementType().getName());
        } else {
            r.setRetirementType("—");
        }

        if (d.getPensionEligibility() != null) {
            r.setPensionEligibilityId(d.getPensionEligibility().getId());
            r.setPensionEligibility(d.getPensionEligibility().getName());
        } else {
            r.setPensionEligibility("—");
        }

        r.setPensionNumber(d.getPensionNumber());
        r.setRetirementOrder(d.getRetirementOrder());
        r.setRetirementBenefits(d.getRetirementBenefits());

        r.setIsActive(d.getIsActive());

        r.setDocumentPath(d.getDocumentPath());
        r.setDocumentName(d.getDocumentName());

        r.setProcessedBy(d.getProcessedBy());

        return r;
    }
}
