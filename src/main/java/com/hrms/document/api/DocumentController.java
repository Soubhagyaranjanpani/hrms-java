package com.hrms.document.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.document.application.*;
import com.hrms.document.dto.DocumentResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final UploadDocumentUseCase uploadUseCase;
    private final GetEmployeeDocumentsUseCase getUseCase;
    private final ExtractDocumentDataUseCase extractUseCase;
    private final ViewDownloadDocumentUseCase viewDownloadUseCase;

    // 🔥 Upload Document
    @Operation(summary = "Upload employee document")
    @PostMapping("/upload")
    public ApiResponse<String> upload(
            @RequestParam Long employeeId,
            @RequestParam String category,
            @RequestParam MultipartFile file,
            Principal principal) {

        String result = uploadUseCase.execute(
                employeeId,
                category,
                file,
                principal.getName()
        );

        return ResponseUtils.createSuccessResponse(
                result,
                new TypeReference<String>() {}
        );
    }

    // 🔥 Get all documents of employee
    @Operation(summary = "Get all documents for an employee")
    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<DocumentResponse>> getDocuments(
            @PathVariable Long employeeId) {

        List<DocumentResponse> data = getUseCase.execute(employeeId);

        return ResponseUtils.createSuccessResponse(
                data,
                new TypeReference<List<DocumentResponse>>() {}
        );
    }

    // 🔥 OCR extraction
    @Operation(summary = "Extract data from document using OCR")
    @PostMapping("/extract/{documentId}")
    public ApiResponse<String> extract(
            @PathVariable Long documentId) {

        String result = extractUseCase.execute(documentId);

        return ResponseUtils.createSuccessResponse(
                result,
                new TypeReference<String>() {}
        );
    }

    @Operation(summary = "View or Download document (mode=V for view, mode=D for download)")
    @GetMapping("/file/{documentId}")
    public ResponseEntity<Resource> getDocumentFile(
            @PathVariable Long documentId,
            @RequestParam(defaultValue = "V") String mode) {

        return viewDownloadUseCase.execute(documentId, mode);
    }
}
