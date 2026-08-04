// File: com/hrms/training/application/UploadTrainingDocumentUseCase.java
package com.hrms.training.application;

import com.hrms.training.domain.TrainingRecord;
import com.hrms.training.infrastructure.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadTrainingDocumentUseCase {

    private final TrainingRepository trainingRepo;
    private final TrainingDocumentStorageService storageService;

    public void execute(Long id, MultipartFile file) throws Exception {
        TrainingRecord r = trainingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        String path = storageService.saveUploaded(id, file);

        r.setDocumentPath(path);
        r.setDocumentName(file.getOriginalFilename());
        trainingRepo.save(r);
    }
}
