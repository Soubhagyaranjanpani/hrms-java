// File: com/hrms/retirement/application/DownloadRetirementDocumentUseCase.java
package com.hrms.retirement.application;

import com.hrms.retirement.domain.RetirementRecord;
import com.hrms.retirement.infrastructure.RetirementRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DownloadRetirementDocumentUseCase {

    private final RetirementRepository retirementRepo;
    private final RetirementDocumentStorageService storageService;

    public DocumentFile execute(Long id) throws Exception {
        RetirementRecord r = retirementRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (r.getDocumentPath() == null || r.getDocumentPath().isBlank()) {
            throw new RuntimeException("No document available for this retirement record");
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
