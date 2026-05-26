package com.hrms.payroll.application;



import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDistinctMonthsUseCase {

    private final PayrollRepository repo;

    public List<String> execute() {
        return repo.findDistinctMonths(); // returns ["2025-04","2025-03","2025-02"]
    }
}
