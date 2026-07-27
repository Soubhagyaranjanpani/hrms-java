package com.hrms.deputation.application;

import com.hrms.deputation.domain.DeputationRecord;
import com.hrms.deputation.dto.DeputationRecordResponse;
import org.springframework.stereotype.Component;

@Component
public class DeputationMapper {

    public DeputationRecordResponse toResponse(DeputationRecord d) {
        DeputationRecordResponse r = new DeputationRecordResponse();
        r.setId(d.getId());
        r.setEmployeeId(d.getEmployee().getId());

        r.setEmployee(d.getEmployee().getFullName());
        r.setEmployeeCode(d.getEmployee().getEmployeeCode());

        r.setDepartment(d.getDepartmentName() != null ? d.getDepartmentName() : "—");
        r.setDesignation(d.getDesignationName() != null ? d.getDesignationName() : "—");

        r.setDeputationOrderNumber(d.getDeputationOrderNumber());
        r.setDeputationOrganization(d.getDeputationOrganization());

        r.setStartDate(d.getStartDate());
        r.setEndDate(d.getEndDate());

        r.setDeputationType(d.getDeputationType());

        if (d.getReportingAuthority() != null && d.getReportingAuthority().getEmployee() != null) {
            r.setReportingAuthority(d.getReportingAuthority().getEmployee().getFullName());
        } else {
            r.setReportingAuthority("—");
        }
        if (d.getReportingAuthority() != null && d.getReportingAuthority().getDesignation() != null) {
            r.setReportingAuthorityDesignation(d.getReportingAuthority().getDesignation().getName());
        } else {
            r.setReportingAuthorityDesignation("—");
        }

        r.setIsActive(d.getIsActive());

        r.setDocumentPath(d.getDocumentPath());
        r.setDocumentName(d.getDocumentName());

        r.setProcessedBy(d.getProcessedBy());

        return r;
    }
}
