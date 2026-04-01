package com.hrms.document.api;

import com.hrms.document.application.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final UploadDocumentUseCase uploadUseCase;
    private final GetEmployeeDocumentsUseCase getUseCase;
    private final ExtractDocumentDataUseCase extractUseCase;

    // 🔥 Upload
    @PostMapping("/upload")
    public String upload(
            @RequestParam Long employeeId,
            @RequestParam String category,
            @RequestParam MultipartFile file,
            Principal principal) {

        return uploadUseCase.execute(
                employeeId,
                category,
                file,
                principal.getName()
        );
    }

    // 🔥 Get all documents of employee
    @GetMapping("/employee/{employeeId}")
    public Object getDocuments(@PathVariable Long employeeId) {
        return getUseCase.execute(employeeId);
    }

    // 🔥 OCR extraction
    @PostMapping("/extract/{documentId}")
    public String extract(@PathVariable Long documentId) {
        return extractUseCase.execute(documentId);
    }
}
