package com.hrms.deputation.application;

import com.hrms.deputation.dto.DeputationRecordResponse;
import com.hrms.deputation.infrastructure.DeputationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEmployeeDeputationHistoryUseCase {

    private final DeputationRepository repo;
    private final DeputationMapper mapper;

    public List<DeputationRecordResponse> execute(Long empId) {
        return repo.findByEmployee_IdAndIsDeletedFalseOrderByStartDateDesc(empId)
                .stream().map(mapper::toResponse).toList();
    }
}