package com.hrms.deputation.application;

import com.hrms.deputation.domain.DeputationRecord;
import com.hrms.deputation.dto.DeputationRecordResponse;
import com.hrms.master.dto.DeputationTypeResponse;
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

        // Map DeputationType
        if (d.getDeputationType() != null) {
            DeputationTypeResponse typeResponse = new DeputationTypeResponse();
            typeResponse.setId(d.getDeputationType().getId());
            typeResponse.setName(d.getDeputationType().getName());
            typeResponse.setIsActive(d.getDeputationType().getIsActive());
            r.setDeputationType(typeResponse);
        }

        // Map Reporting Authority
        if (d.getReportingAuthority() != null) {
            if (d.getReportingAuthority().getEmployee() != null) {
                r.setReportingAuthority(d.getReportingAuthority().getEmployee().getFullName());
            }
            if (d.getReportingAuthority().getDesignation() != null) {
                r.setReportingAuthorityDesignation(d.getReportingAuthority().getDesignation().getName());
            }
        } else {
            r.setReportingAuthority("—");
            r.setReportingAuthorityDesignation("—");
        }

        r.setIsActive(d.getIsActive());
        r.setDocumentPath(d.getDocumentPath());
        r.setDocumentName(d.getDocumentName());
        r.setRemarks(d.getRemarks());
        r.setProcessedBy(d.getProcessedBy());

        return r;
    }
}