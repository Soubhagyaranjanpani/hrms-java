package com.hrms.promotion.application;

import com.hrms.promotion.domain.PromotionRecord;
import com.hrms.promotion.dto.PromotionRecordResponse;
import com.hrms.promotion.dto.UpdatePromotionRequest;
import com.hrms.promotion.infrastructure.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatePromotionRecordUseCase {

    private final PromotionRepository repo;
    private final PromotionMapper     mapper;

    public PromotionRecordResponse execute(Long id, UpdatePromotionRequest req) {
        PromotionRecord p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion record not found"));

        if (req.getPromotionOrderNumber() != null) p.setPromotionOrderNumber(req.getPromotionOrderNumber());
//        if (req.getPromotionType()        != null) p.setPromotionType(req.getPromotionType());
//
//        if (req.getOldDesignation()       != null) p.setOldDesignation(req.getOldDesignation());
//        if (req.getNewDesignation()       != null) p.setNewDesignation(req.getNewDesignation());
//        if (req.getOldDepartment()        != null) p.setOldDepartment(req.getOldDepartment());
//        if (req.getNewDepartment()        != null) p.setNewDepartment(req.getNewDepartment());

//        if (req.getPreviousGrade()        != null) p.setPreviousGrade(req.getPreviousGrade());
//        if (req.getNewGrade()             != null) p.setNewGrade(req.getNewGrade());

        if (req.getOldSalary()            != null) p.setOldSalary(req.getOldSalary());
        if (req.getNewSalary()            != null) p.setNewSalary(req.getNewSalary());

        if (req.getPromotionDate()        != null) p.setPromotionDate(req.getPromotionDate());
        if (req.getEffectiveDate()        != null) p.setEffectiveDate(req.getEffectiveDate());

//        if (req.getPromotionAuthority()   != null) p.setPromotionAuthority(req.getPromotionAuthority());
        if (req.getRemarks()              != null) p.setRemarks(req.getRemarks());

        return mapper.toResponse(repo.save(p));
    }
}