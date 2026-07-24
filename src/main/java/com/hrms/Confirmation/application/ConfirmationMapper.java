package com.hrms.confirmation.application;

import com.hrms.confirmation.domain.ConfirmationRecord;
import com.hrms.confirmation.dto.ConfirmationRecordResponse;
import org.springframework.stereotype.Component;

@Component
public class ConfirmationMapper {

    public ConfirmationRecordResponse toResponse(ConfirmationRecord c) {
        ConfirmationRecordResponse r = new ConfirmationRecordResponse();
        r.setId(c.getId());
        r.setEmployeeId(c.getEmployee().getId());

        r.setEmployee(c.getEmployee().getFullName());
        r.setEmployeeCode(c.getEmployee().getEmployeeCode());

        r.setDepartment(c.getDepartmentName() != null ? c.getDepartmentName() : "—");
        r.setDesignation(c.getDesignationName() != null ? c.getDesignationName() : "—");

        r.setConfirmationOrderNumber(c.getConfirmationOrderNumber());
        r.setConfirmationDate(c.getConfirmationDate());

        if (c.getConfirmedBy() != null && c.getConfirmedBy().getEmployee() != null) {
            r.setConfirmedBy(c.getConfirmedBy().getEmployee().getFullName());
        } else {
            r.setConfirmedBy("—");
        }
        if (c.getConfirmedBy() != null && c.getConfirmedBy().getDesignation() != null) {
            r.setConfirmedByDesignation(c.getConfirmedBy().getDesignation().getName());
        } else {
            r.setConfirmedByDesignation("—");
        }

        r.setIsActive(c.getIsActive());

        r.setDocumentPath(c.getDocumentPath());
        r.setDocumentName(c.getDocumentName());

        r.setRemarks(c.getRemarks());
        r.setProcessedBy(c.getProcessedBy());

        return r;
    }
}
