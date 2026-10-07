package com.hrms.performance.application;

import com.hrms.performance.domain.EmployeeKPI;
import com.hrms.performance.infrastructure.EmployeeKPIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculatePerformanceUseCase {

    private final EmployeeKPIRepository repo;

    public Double execute(Long employeeId) {

        List<EmployeeKPI> list = repo.findByEmployee_Id(employeeId);

        return list.stream()
                .mapToDouble(k ->
                        (k.getAchieved() / k.getTarget()) * k.getKpi().getWeightage()
                )
                .sum();
    }
}
