package com.hrms.transfer.application;

import com.hrms.master.dto.TransferTypeResponse;
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

        // ✅ Map TransferType to response
        if (t.getTransferType() != null) {
            TransferTypeResponse typeResponse = new TransferTypeResponse();
            typeResponse.setId(t.getTransferType().getId());
            typeResponse.setName(t.getTransferType().getName());
            typeResponse.setIsActive(t.getTransferType().getIsActive());
            r.setTransferType(typeResponse);
        }

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