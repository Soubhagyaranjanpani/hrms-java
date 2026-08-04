package com.hrms.training.application;

import com.hrms.training.domain.TrainingRecord;
import com.hrms.training.dto.TrainingRecordResponse;
import com.hrms.training.dto.UpdateTrainingRequest;
import com.hrms.training.infrastructure.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTrainingRecordUseCase {

    private final TrainingRepository repo;
    private final TrainingMapper mapper;

    public TrainingRecordResponse execute(Long id, UpdateTrainingRequest req) {
        TrainingRecord t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Training record not found"));

        if (req.getTrainingName() != null) t.setTrainingName(req.getTrainingName());
        if (req.getProvider() != null) t.setProvider(req.getProvider());
        if (req.getStartDate() != null) t.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) t.setEndDate(req.getEndDate());
        if (req.getHours() != null) t.setHours(req.getHours());
        if (req.getCertification() != null) t.setCertification(req.getCertification());

        return mapper.toResponse(repo.save(t));
    }
}
