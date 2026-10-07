package com.hrms.payroll.application;

import com.hrms.payroll.dto.PayrollRecordResponse;
import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPayrollListUseCase {

    private final PayrollRepository repo;
    private final PayrollMapper     mapper;

    public List<PayrollRecordResponse> execute(String yearMonth) {
        return repo.findByYearMonthAndIsDeletedFalse(yearMonth)
                .stream().map(mapper::toResponse).toList();
    }
}