// File: com/hrms/deputation/application/UploadDeputationDocumentUseCase.java
package com.hrms.deputation.application;

import com.hrms.deputation.domain.DeputationRecord;
import com.hrms.deputation.infrastructure.DeputationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadDeputationDocumentUseCase {

    private final DeputationRepository deputationRepo;
    private final DeputationDocumentStorageService storageService;

    public void execute(Long id, MultipartFile file) throws Exception {
        DeputationRecord r = deputationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        String path = storageService.saveUploaded(id, file);

        r.setDocumentPath(path);
        r.setDocumentName(file.getOriginalFilename());
        deputationRepo.save(r);
    }
}
