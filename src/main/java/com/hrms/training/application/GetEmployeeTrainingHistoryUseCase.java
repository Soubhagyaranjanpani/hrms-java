package com.hrms.training.application;

import com.hrms.training.dto.TrainingRecordResponse;
import com.hrms.training.infrastructure.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEmployeeTrainingHistoryUseCase {

    private final TrainingRepository repo;
    private final TrainingMapper mapper;

    public List<TrainingRecordResponse> execute(Long empId) {
        return repo.findByEmployee_IdAndIsDeletedFalseOrderByStartDateDesc(empId)
                .stream().map(mapper::toResponse).toList();
    }
}
