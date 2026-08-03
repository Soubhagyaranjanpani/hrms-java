// File: com/hrms/retirement/application/UploadRetirementDocumentUseCase.java
package com.hrms.retirement.application;

import com.hrms.retirement.domain.RetirementRecord;
import com.hrms.retirement.infrastructure.RetirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadRetirementDocumentUseCase {

    private final RetirementRepository retirementRepo;
    private final RetirementDocumentStorageService storageService;

    public void execute(Long id, MultipartFile file) throws Exception {
        RetirementRecord r = retirementRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        String path = storageService.saveUploaded(id, file);

        r.setDocumentPath(path);
        r.setDocumentName(file.getOriginalFilename());
        retirementRepo.save(r);
    }
}
