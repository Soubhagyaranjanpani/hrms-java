package com.hrms.payroll.application;





import com.hrms.payroll.dto.PayrollRecordResponse;
import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEmployeePayHistoryUseCase {

    private final PayrollRepository repo;
    private final PayrollMapper     mapper;

    public List<PayrollRecordResponse> execute(Long empId) {
        return repo.findByEmployee_IdAndIsDeletedFalseOrderByYearMonthDesc(empId)
                .stream().map(mapper::toResponse).toList();
    }
}
