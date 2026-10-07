package com.hrms.document.application;

import com.hrms.document.domain.EmployeeDocument;
import com.hrms.document.infrastructure.EmployeeDocumentRepository;
import com.hrms.audit.application.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExtractDocumentDataUseCase {

    private final EmployeeDocumentRepository repo;
    private final AuditLogService audit;

    public String execute(Long documentId) {

        EmployeeDocument doc = repo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // 🔥 Dummy OCR (replace with real OCR later)
        String extractedText = "Sample OCR text";

        doc.setExtractedText(extractedText);
        doc.setDocumentType("AUTO_DETECTED");

        repo.save(doc);

        audit.log("DOCUMENT", doc.getId(), "OCR_EXTRACTED", "SYSTEM", null, extractedText);

        return "OCR extraction completed";
    }
}
