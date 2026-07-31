package com.hrms.Awards_Recognition.application;

import com.hrms.Awards_Recognition.domain.AwardRecord;
import com.hrms.Awards_Recognition.infrastructure.AwardRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DownloadAwardDocumentUseCase {

    private final AwardRepository awardRepo;
    private final AwardDocumentStorageService storageService;

    public DocumentFile execute(Long id) throws Exception {
        AwardRecord r = awardRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (r.getDocumentPath() == null || r.getDocumentPath().isBlank()) {
            throw new RuntimeException("No document available for this award record");
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