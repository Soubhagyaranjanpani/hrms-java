// File: com/hrms/promotion/application/DownloadPromotionDocumentUseCase.java
package com.hrms.promotion.application;

import com.hrms.promotion.domain.PromotionRecord;
import com.hrms.promotion.infrastructure.PromotionRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DownloadPromotionDocumentUseCase {

    private final PromotionRepository promoRepo;
    private final PromotionDocumentStorageService storageService;

    public DocumentFile execute(Long id) throws Exception {
        PromotionRecord r = promoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (r.getDocumentPath() == null || r.getDocumentPath().isBlank()) {
            throw new RuntimeException("No document available for this promotion record");
        }

        byte[] bytes = storageService.readFile(r.getDocumentPath());
        String fileName = r.getDocumentName() != null ? r.getDocumentName() : storageService.fileNameOf(r.getDocumentPath());
        String contentType = fileName.toLowerCase().endsWith(".pdf") ? "application/pdf" : "application/octet-stream";

        return new DocumentFile(bytes, fileName, contentType);
    }

    @Getter
    public static class DocumentFile {
        private final byte[] bytes;
        private final String fileName;
        private final String contentType;

        public DocumentFile(byte[] bytes, String fileName, String contentType) {
            this.bytes = bytes;
            this.fileName = fileName;
            this.contentType = contentType;
        }
    }
}