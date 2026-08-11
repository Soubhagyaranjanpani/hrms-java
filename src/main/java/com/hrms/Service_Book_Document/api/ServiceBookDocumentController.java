// path: src/main/java/com/hrms/Service_Book_Document/api/ServiceBookDocumentController.java
package com.hrms.Service_Book_Document.api;

import com.hrms.Service_Book_Document.application.ServiceBookDocumentService;
import com.hrms.Service_Book_Document.dto.DocumentRecordDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/service-books")
@RequiredArgsConstructor
public class ServiceBookDocumentController {

    private final ServiceBookDocumentService documentService;

    @GetMapping("/employees/{employeeId}/documents")
    public ResponseEntity<List<DocumentRecordDTO>> getDocuments(@PathVariable Long employeeId) {
        return ResponseEntity.ok(documentService.getDocumentsForEmployee(employeeId));
    }

    @GetMapping("/employees/{employeeId}/documents/grouped")
    public ResponseEntity<Map<String, List<DocumentRecordDTO>>> getGroupedDocuments(
            @PathVariable Long employeeId) {
        List<DocumentRecordDTO> docs = documentService.getDocumentsForEmployee(employeeId);
        Map<String, List<DocumentRecordDTO>> grouped = docs.stream()
                .collect(Collectors.groupingBy(DocumentRecordDTO::getCategory));
        return ResponseEntity.ok(grouped);
    }
}