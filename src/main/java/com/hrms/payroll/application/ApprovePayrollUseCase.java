package com.hrms.payroll.application;



import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovePayrollUseCase {

    private final PayrollRepository repo;

    public String execute(String yearMonth, List<Long> recordIds, String approverEmail) {
        List<PayrollRecord> records = recordIds != null && !recordIds.isEmpty()
                ? repo.findAllById(recordIds)
                : repo.findByYearMonthAndStatusAndIsDeletedFalse(yearMonth, "PENDING");

        records.forEach(r -> {
            r.setStatus("APPROVED");
            r.setProcessedBy(approverEmail);
        });
        repo.saveAll(records);
        return records.size() + " records approved";
    }
}
