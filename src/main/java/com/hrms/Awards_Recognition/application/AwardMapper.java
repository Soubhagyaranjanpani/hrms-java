package com.hrms.Awards_Recognition.application;


import com.hrms.Awards_Recognition.domain.AwardRecord;
import com.hrms.Awards_Recognition.dto.AwardRecordResponse;
import com.hrms.master.dto.AwardTypeResponse;
import org.springframework.stereotype.Component;

@Component
public class AwardMapper {

    public AwardRecordResponse toResponse(AwardRecord a) {
        AwardRecordResponse r = new AwardRecordResponse();
        r.setId(a.getId());
        r.setEmployeeId(a.getEmployee().getId());

        // Full name from firstName + lastName
        String fullName = (a.getEmployee().getFirstName() != null ? a.getEmployee().getFirstName() : "") +
                " " + (a.getEmployee().getLastName() != null ? a.getEmployee().getLastName() : "");
        r.setEmployeeName(fullName.trim());

        r.setEmployeeCode(a.getEmployee().getEmployeeCode());

        r.setDepartmentName(a.getDepartmentName() != null ? a.getDepartmentName() : "—");
        r.setDesignationName(a.getDesignationName() != null ? a.getDesignationName() : "—");

        r.setAwardName(a.getAwardName());
        r.setAwardDate(a.getAwardDate());
        r.setDescription(a.getDescription());

        // ✅ Map Award Type - Dropdown 1
        if (a.getAwardType() != null) {
            AwardTypeResponse typeResponse = new AwardTypeResponse();
            typeResponse.setId(a.getAwardType().getId());
            typeResponse.setName(a.getAwardType().getName());
            typeResponse.setIsActive(a.getAwardType().getIsActive());
            r.setAwardType(typeResponse);
        }

        // ✅ Map Issued By - Dropdown 2
        if (a.getIssuedBy() != null) {
            r.setIssuedById(a.getIssuedBy().getId());
            if (a.getIssuedBy().getEmployee() != null) {
                String issuedByName = (a.getIssuedBy().getEmployee().getFirstName() != null ?
                        a.getIssuedBy().getEmployee().getFirstName() : "") +
                        " " + (a.getIssuedBy().getEmployee().getLastName() != null ?
                        a.getIssuedBy().getEmployee().getLastName() : "");
                r.setIssuedByName(issuedByName.trim());
            }
            if (a.getIssuedBy().getDesignation() != null) {
                r.setIssuedByDesignation(a.getIssuedBy().getDesignation().getName());
            }
        }

        r.setIsActive(a.getIsActive());
        r.setDocumentPath(a.getDocumentPath());
        r.setDocumentName(a.getDocumentName());
        r.setProcessedBy(a.getProcessedBy());
        r.setCreatedAt(a.getCreatedAt());
        r.setUpdatedAt(a.getUpdatedAt());

        return r;
    }
}