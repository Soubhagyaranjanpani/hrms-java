package com.hrms.retirement.application;

import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.retirement.domain.RetirementRecord;
import com.hrms.retirement.dto.RetirementRecordResponse;
import com.hrms.retirement.dto.UpdateRetirementRequest;
import com.hrms.retirement.infrastructure.RetirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateRetirementRecordUseCase {

    private final RetirementRepository repo;
    private final EmployeeRepository empRepo;
    private final RetirementMapper mapper;
    private final AuditService auditService;

    public RetirementRecordResponse execute(Long id, UpdateRetirementRequest req) {
        RetirementRecord r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Retirement record not found"));

        // Capture old values for audit
        String oldPensionNumber = r.getPensionNumber();
        String oldRetirementOrder = r.getRetirementOrder();
        String oldRetirementBenefits = r.getRetirementBenefits();

        // Update fields
        if (req.getRetirementDate() != null) r.setRetirementDate(req.getRetirementDate());
        if (req.getPensionNumber() != null) r.setPensionNumber(req.getPensionNumber());
        if (req.getRetirementOrder() != null) r.setRetirementOrder(req.getRetirementOrder());
        if (req.getRetirementBenefits() != null) r.setRetirementBenefits(req.getRetirementBenefits());

        RetirementRecord saved = repo.save(r);

        // Employee ke flags confirm/update karein
        Employee emp = saved.getEmployee();
        if (emp != null) {
            emp.setIsRetirement(true);
            emp.setIsActive(false);
            empRepo.save(emp);
        }

        // Build change summary
        List<String> changes = new ArrayList<>();

        if (req.getRetirementDate() != null) {
            changes.add("Retirement Date updated");
        }

        if (oldPensionNumber != null && !oldPensionNumber.equals(saved.getPensionNumber())) {
            changes.add("Pension Number: " + oldPensionNumber + " → " + saved.getPensionNumber());
        }

        if (oldRetirementOrder != null && !oldRetirementOrder.equals(saved.getRetirementOrder())) {
            changes.add("Retirement Order: " + oldRetirementOrder + " → " + saved.getRetirementOrder());
        }

        if (oldRetirementBenefits != null && !oldRetirementBenefits.equals(saved.getRetirementBenefits())) {
            changes.add("Benefits updated");
        }

        // Audit Log - Single consolidated entry
        if (!changes.isEmpty()) {
            auditService.log(
                    AuditModule.RETIREMENT,
                    AuditAction.UPDATE,
                    "Retirement record updated for " +
                            (emp != null ? emp.getFullName() +
                                           " (" + emp.getEmployeeCode() + ")" : "record"),
                    emp,
                    "Multiple Fields",
                    oldRetirementOrder,
                    saved.getRetirementOrder(),
                    String.join("; ", changes),
                    saved.getId()
            );
        }

        // Employee status confirmation audit
        if (emp != null) {
            auditService.log(
                    AuditModule.EMPLOYEE,
                    AuditAction.UPDATE,
                    "Employee retirement status confirmed: " + emp.getFullName(),
                    emp,
                    "Employment Status",
                    "Active",
                    "Retired",
                    "Retirement record updated",
                    emp.getId()
            );
        }

        return mapper.toResponse(saved);
    }
}