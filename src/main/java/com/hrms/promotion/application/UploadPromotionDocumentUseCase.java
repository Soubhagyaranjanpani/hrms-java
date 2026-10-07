// File: com/hrms/promotion/application/UploadPromotionDocumentUseCase.java
package com.hrms.promotion.application;

import com.hrms.promotion.domain.PromotionRecord;
import com.hrms.promotion.infrastructure.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadPromotionDocumentUseCase {

    private final PromotionRepository promoRepo;
    private final PromotionDocumentStorageService storageService;

    public void execute(Long id, MultipartFile file) throws Exception {
        PromotionRecord r = promoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        String path = storageService.saveUploaded(id, file);

        r.setDocumentPath(path);
        r.setDocumentName(file.getOriginalFilename());
        promoRepo.save(r);
    }
}