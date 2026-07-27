package com.hrms.payrevision.application;

import com.hrms.payrevision.domain.PayRevisionRecord;
import com.hrms.payrevision.dto.PayRevisionRecordResponse;
import org.springframework.stereotype.Component;

@Component
public class PayRevisionMapper {

    public PayRevisionRecordResponse toResponse(PayRevisionRecord p) {
        PayRevisionRecordResponse r = new PayRevisionRecordResponse();
        r.setId(p.getId());
        r.setEmployeeId(p.getEmployee().getId());

        r.setEmployee(p.getEmployee().getFullName());
        r.setEmployeeCode(p.getEmployee().getEmployeeCode());

        r.setPayRevisionOrderNumber(p.getPayRevisionOrderNumber());
        r.setEffectiveDate(p.getEffectiveDate());

        r.setPreviousPayScaleMin(p.getPreviousPayScaleMin());
        r.setPreviousPayScaleMax(p.getPreviousPayScaleMax());
        r.setRevisedPayScaleMin(p.getRevisedPayScaleMin());
        r.setRevisedPayScaleMax(p.getRevisedPayScaleMax());

        r.setIncrementAmount(p.getIncrementAmount());
        r.setIncrementPercent(p.getIncrementPercent());

        if (p.getReason() != null) {
            r.setReasonId(p.getReason().getId());
            r.setReason(p.getReason().getName());
        } else {
            r.setReason("—");
        }

        r.setIsActive(p.getIsActive());

        r.setDocumentPath(p.getDocumentPath());
        r.setDocumentName(p.getDocumentName());

        r.setRemarks(p.getRemarks());
        r.setProcessedBy(p.getProcessedBy());

        return r;
    }
}
