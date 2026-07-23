package com.hrms.promotion.application;

import com.hrms.employee.infrastructure.EmployeeRepository; // ⚠️ apne actual package se confirm karo
import com.hrms.promotion.domain.PromotionHistory;
import com.hrms.promotion.dto.PromotionHistoryRequest;
import com.hrms.promotion.dto.PromotionHistoryResponse;
import com.hrms.promotion.infrastructure.PromotionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavePromotionUseCase {

    private final PromotionHistoryRepository promoRepo;
    private final EmployeeRepository employeeRepo;

    @Transactional
    public PromotionHistoryResponse execute(PromotionHistoryRequest request) {

        var employee = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Employee not found: " + request.getEmployeeId()));

        if (promoRepo.existsByPromotionOrderNumber(request.getPromotionOrderNumber())) {
            throw new IllegalArgumentException(
                    "Promotion order number already exists: " + request.getPromotionOrderNumber());
        }

        PromotionHistory record = PromotionHistory.builder()
                .employeeId(employee.getId())
                .oldBranchId(request.getOldBranchId())
                .newBranchId(request.getNewBranchId())
                .oldDepartmentId(request.getOldDepartmentId())
                .newDepartmentId(request.getNewDepartmentId())
                .oldDesignationId(request.getOldDesignationId())
                .newDesignationId(request.getNewDesignationId())
                .promotionOrderNumber(request.getPromotionOrderNumber())
                .promotionDate(request.getPromotionDate())
                .promotionTypeId(request.getPromotionTypeId())
                .oldGrade(request.getOldGrade())
                .newGrade(request.getNewGrade())
                .effectiveDate(request.getEffectiveDate())
                .promotionAuthorityId(request.getPromotionAuthorityId())
                .isDeleted(false)
                .flag(1)
                .build();

        PromotionHistory saved = promoRepo.save(record);

        return PromotionHistoryResponse.builder()
                .id(saved.getId())
                .employeeId(saved.getEmployeeId())
                .employeeName(employee.getFullName()) // ⚠️ apne Employee entity ke actual getter se confirm karo
                .oldBranchId(saved.getOldBranchId())
                .newBranchId(saved.getNewBranchId())
                .oldDepartmentId(saved.getOldDepartmentId())
                .newDepartmentId(saved.getNewDepartmentId())
                .oldDesignationId(saved.getOldDesignationId())
                .newDesignationId(saved.getNewDesignationId())
                .promotionOrderNumber(saved.getPromotionOrderNumber())
                .promotionDate(saved.getPromotionDate())
                .promotionTypeId(saved.getPromotionTypeId())
                .oldGrade(saved.getOldGrade())
                .newGrade(saved.getNewGrade())
                .effectiveDate(saved.getEffectiveDate())
                .promotionAuthorityId(saved.getPromotionAuthorityId())
                .status(Boolean.TRUE.equals(saved.getIsDeleted()) ? "INACTIVE" : "ACTIVE")
                .build();
    }
}