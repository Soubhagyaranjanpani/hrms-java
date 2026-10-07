// File: com/hrms/payrevision/application/UploadPayRevisionDocumentUseCase.java
package com.hrms.payrevision.application;

import com.hrms.payrevision.domain.PayRevisionRecord;
import com.hrms.payrevision.infrastructure.PayRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadPayRevisionDocumentUseCase {

    private final PayRevisionRepository payRevisionRepo;
    private final PayRevisionDocumentStorageService storageService;

    public void execute(Long id, MultipartFile file) throws Exception {
        PayRevisionRecord r = payRevisionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        String path = storageService.saveUploaded(id, file);

        r.setDocumentPath(path);
        r.setDocumentName(file.getOriginalFilename());
        payRevisionRepo.save(r);
    }
}
