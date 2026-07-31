package com.hrms.Awards_Recognition.application;

import com.hrms.Awards_Recognition.domain.AwardRecord;
import com.hrms.Awards_Recognition.dto.AwardRecordResponse;
import com.hrms.Awards_Recognition.dto.UpdateAwardRequest;
import com.hrms.Awards_Recognition.infrastructure.AwardRepository;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.master.infrastructure.AwardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAwardRecordUseCase {

    private final AwardRepository repo;
    private final AwardMapper mapper;
    private final AwardTypeRepository awardTypeRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;

    @Transactional
    public AwardRecordResponse execute(Long id, UpdateAwardRequest req) {
        AwardRecord a = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Award record not found with ID: " + id));

        if (req.getAwardName() != null) {
            a.setAwardName(req.getAwardName());
        }
        if (req.getAwardDate() != null) {
            a.setAwardDate(req.getAwardDate());
        }
        if (req.getDescription() != null) {
            a.setDescription(req.getDescription());
        }

        // ✅ Update Dropdown 1 - Award Type
        if (req.getAwardTypeId() != null) {
            var awardType = awardTypeRepo.findById(req.getAwardTypeId())
                    .orElseThrow(() -> new RuntimeException("Award Type not found with ID: " + req.getAwardTypeId()));
            a.setAwardType(awardType);
        }

        // ✅ Update Dropdown 2 - Issued By
        if (req.getIssuedById() != null) {
            var issuedBy = employeeDesignationRepo.findById(req.getIssuedById())
                    .orElseThrow(() -> new RuntimeException("Issued By not found with ID: " + req.getIssuedById()));
            a.setIssuedBy(issuedBy);
        }

        return mapper.toResponse(repo.save(a));
    }
}