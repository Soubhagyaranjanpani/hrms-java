package com.hrms.payroll.application;

import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.payroll.dto.ProcessPayrollRequest;
import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessPayrollUseCase {

    private final PayrollRepository repo;

    public String execute(ProcessPayrollRequest req, String processorEmail) {
        LocalDate payDate = req.getPaymentDate() != null ? req.getPaymentDate() : LocalDate.now();

        List<PayrollRecord> records = req.getRecordIds() != null && !req.getRecordIds().isEmpty()
                ? repo.findAllById(req.getRecordIds())
                : repo.findByYearMonthAndStatusAndIsDeletedFalse(req.getYearMonth(), "APPROVED");

        records.forEach(r -> {
            r.setStatus("PROCESSED");
            r.setPaymentDate(payDate);
            r.setProcessedBy(processorEmail);
        });
        repo.saveAll(records);
        return records.size() + " records processed, payment date: " + payDate;
    }
}