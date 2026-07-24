// File: com/hrms/confirmation/application/DownloadConfirmationDocumentUseCase.java
package com.hrms.confirmation.application;

import com.hrms.confirmation.domain.ConfirmationRecord;
import com.hrms.confirmation.infrastructure.ConfirmationRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DownloadConfirmationDocumentUseCase {

    private final ConfirmationRepository confirmationRepo;
    private final ConfirmationDocumentStorageService storageService;

    public DocumentFile execute(Long id) throws Exception {
        ConfirmationRecord r = confirmationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (r.getDocumentPath() == null || r.getDocumentPath().isBlank()) {
            throw new RuntimeException("No document available for this confirmation record");
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
