package com.hrms.promotion.application;

import com.hrms.promotion.domain.PromotionRecord;
import com.hrms.promotion.dto.PromotionRecordResponse;
import org.springframework.stereotype.Component;

@Component
public class PromotionMapper {

    public PromotionRecordResponse toResponse(PromotionRecord p) {
        PromotionRecordResponse r = new PromotionRecordResponse();
        r.setId(p.getId());
        r.setEmployeeId(p.getEmployee().getId());

        // FIX: use getFullName() instead of manual concatenation — no more "soubhagyanull"
        r.setEmployee(p.getEmployee().getFullName());
        r.setEmployeeCode(p.getEmployee().getEmployeeCode());
        r.setBranch(p.getEmployee().getBranch() != null ? p.getEmployee().getBranch().getName() : "—");

        r.setPromotionYear(p.getPromotionYear());
        r.setPromotionOrderNumber(p.getPromotionOrderNumber());

        // FIX: PromotionType's actual getter is getPromotionTypeName(), not getName()
        r.setPromotionType(p.getPromotionType() != null ? p.getPromotionType().getPromotionTypeName() : "—");

        r.setOldDesignation(p.getOldDesignation() != null ? p.getOldDesignation().getName() : "—");
        r.setNewDesignation(p.getNewDesignation() != null ? p.getNewDesignation().getName() : "—");
        r.setOldDepartment(p.getOldDepartment() != null ? p.getOldDepartment().getName() : "—");
        r.setNewDepartment(p.getNewDepartment() != null ? p.getNewDepartment().getName() : "—");

        // NEW: branch
        r.setOldBranch(p.getOldBranch() != null ? p.getOldBranch().getName() : "—");
        r.setNewBranch(p.getNewBranch() != null ? p.getNewBranch().getName() : "—");

        r.setPreviousGrade(p.getPreviousGrade() != null ? p.getPreviousGrade().getName() : "—");
        r.setNewGrade(p.getNewGrade() != null ? p.getNewGrade().getName() : "—");

        r.setOldSalary(p.getOldSalary());
        r.setNewSalary(p.getNewSalary());
        r.setIncrementAmount(p.getIncrementAmount());
        r.setIncrementPercent(p.getIncrementPercent());

        r.setPromotionDate(p.getPromotionDate());
        r.setEffectiveDate(p.getEffectiveDate());

        // FIX: use getFullName() on the authority's employee, not raw firstName+lastName
        if (p.getPromotionAuthority() != null && p.getPromotionAuthority().getEmployee() != null) {
            r.setPromotionAuthority(p.getPromotionAuthority().getEmployee().getFullName());
        } else {
            r.setPromotionAuthority("—");
        }
        // NEW: the authority's role/designation, separate from their name
        if (p.getPromotionAuthority() != null && p.getPromotionAuthority().getDesignation() != null) {
            r.setPromotionAuthorityDesignation(p.getPromotionAuthority().getDesignation().getName());
        } else {
            r.setPromotionAuthorityDesignation("—");
        }

        r.setIsActive(p.getIsActive());

        r.setDocumentPath(p.getDocumentPath());
        r.setDocumentName(p.getDocumentName());

        r.setRemarks(p.getRemarks());
        r.setAiInsight(p.getAiInsight());
        r.setProcessedBy(p.getProcessedBy());

        return r;
    }
}