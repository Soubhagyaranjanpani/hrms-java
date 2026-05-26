package com.hrms.payroll.application;

import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmitPayrollForApprovalUseCase {

    private final PayrollRepository payrollRepo;

    @Transactional
    public String execute(List<Long> recordIds) {
        List<PayrollRecord> records = payrollRepo.findAllById(recordIds);
        int count = 0;

        for (PayrollRecord r : records) {
            if ("DRAFT".equals(r.getStatus())) {
                r.setStatus("PENDING");
                count++;
            }
        }

        payrollRepo.saveAll(records);
        return count + " records submitted for approval";
    }
}