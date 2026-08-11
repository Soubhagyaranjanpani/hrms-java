package com.hrms.retirement.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.retirement.domain.RetirementRecord;
import com.hrms.retirement.dto.RetirementRecordResponse;
import com.hrms.retirement.dto.UpdateRetirementRequest;
import com.hrms.retirement.infrastructure.RetirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateRetirementRecordUseCase {

    private final RetirementRepository repo;
    private final EmployeeRepository empRepo;
    private final RetirementMapper mapper;

    public RetirementRecordResponse execute(Long id, UpdateRetirementRequest req) {
        RetirementRecord r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Retirement record not found"));

        if (req.getRetirementDate() != null) r.setRetirementDate(req.getRetirementDate());
        if (req.getPensionNumber() != null) r.setPensionNumber(req.getPensionNumber());
        if (req.getRetirementOrder() != null) r.setRetirementOrder(req.getRetirementOrder());
        if (req.getRetirementBenefits() != null) r.setRetirementBenefits(req.getRetirementBenefits());

        RetirementRecord saved = repo.save(r);

        // ✅ Employee ke flags confirm/update karein — retirement record
        // update hone par employee "retired" aur "inactive" hi rehna chahiye
        Employee emp = saved.getEmployee();
        if (emp != null) {
            emp.setIsRetirement(true);
            emp.setIsActive(false);
            empRepo.save(emp);
        }

        return mapper.toResponse(saved);
    }
}