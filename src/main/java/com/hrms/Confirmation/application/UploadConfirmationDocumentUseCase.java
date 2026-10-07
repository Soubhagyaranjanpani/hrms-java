// File: com/hrms/confirmation/application/UploadConfirmationDocumentUseCase.java
package com.hrms.Confirmation.application;

import com.hrms.Confirmation.application.ConfirmationDocumentStorageService;
import com.hrms.Confirmation.domain.ConfirmationRecord;
import com.hrms.Confirmation.infrastructure.ConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadConfirmationDocumentUseCase {

    private final ConfirmationRepository confirmationRepo;
    private final ConfirmationDocumentStorageService storageService;

    public void execute(Long id, MultipartFile file) throws Exception {
        ConfirmationRecord r = confirmationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        String path = storageService.saveUploaded(id, file);

        r.setDocumentPath(path);
        r.setDocumentName(file.getOriginalFilename());
        confirmationRepo.save(r);
    }
}
