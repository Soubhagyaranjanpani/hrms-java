package com.hrms.Awards_Recognition.application;


import com.hrms.Awards_Recognition.dto.AwardRecordResponse;
import com.hrms.Awards_Recognition.infrastructure.AwardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEmployeeAwardHistoryUseCase {

    private final AwardRepository repo;
    private final AwardMapper mapper;

    public List<AwardRecordResponse> execute(Long empId) {
        return repo.findByEmployee_IdAndIsDeletedFalseOrderByAwardDateDesc(empId)
                .stream().map(mapper::toResponse).toList();
    }
}