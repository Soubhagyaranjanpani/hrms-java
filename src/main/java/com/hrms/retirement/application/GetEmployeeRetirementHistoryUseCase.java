package com.hrms.retirement.application;

import com.hrms.retirement.dto.RetirementRecordResponse;
import com.hrms.retirement.infrastructure.RetirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEmployeeRetirementHistoryUseCase {

    private final RetirementRepository repo;
    private final RetirementMapper mapper;

    public List<RetirementRecordResponse> execute(Long empId) {
        return repo.findByEmployee_IdAndIsDeletedFalseOrderByRetirementDateDesc(empId)
                .stream().map(mapper::toResponse).toList();
    }
}
