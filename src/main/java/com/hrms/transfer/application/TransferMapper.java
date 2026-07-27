package com.hrms.transfer.application;

import com.hrms.transfer.domain.TransferRecord;
import com.hrms.transfer.dto.TransferRecordResponse;
import org.springframework.stereotype.Component;

@Component
public class TransferMapper {

    public TransferRecordResponse toResponse(TransferRecord t) {
        TransferRecordResponse r = new TransferRecordResponse();
        r.setId(t.getId());
        r.setEmployeeId(t.getEmployee().getId());

        r.setEmployee(t.getEmployee().getFullName());
        r.setEmployeeCode(t.getEmployee().getEmployeeCode());
        r.setDesignation(t.getDesignationName() != null ? t.getDesignationName() : "—");

        r.setTransferOrderNumber(t.getTransferOrderNumber());
        r.setTransferDate(t.getTransferDate());
        r.setTransferType(t.getTransferType());

        r.setFromDepartment(t.getFromDepartment() != null ? t.getFromDepartment().getName() : "—");
        r.setToDepartment(t.getToDepartment() != null ? t.getToDepartment().getName() : "—");
        r.setFromBranch(t.getFromBranch() != null ? t.getFromBranch().getName() : "—");
        r.setToBranch(t.getToBranch() != null ? t.getToBranch().getName() : "—");

        r.setEffectiveDate(t.getEffectiveDate());
        r.setTransferReason(t.getTransferReason());

        r.setIsActive(t.getIsActive());

        r.setDocumentPath(t.getDocumentPath());
        r.setDocumentName(t.getDocumentName());

        r.setProcessedBy(t.getProcessedBy());

        return r;
    }
}
