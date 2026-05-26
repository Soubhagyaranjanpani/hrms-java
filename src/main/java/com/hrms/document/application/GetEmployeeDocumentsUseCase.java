package com.hrms.document.application;

import com.hrms.document.dto.DocumentResponse;
import com.hrms.document.infrastructure.EmployeeDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetEmployeeDocumentsUseCase {

    private final EmployeeDocumentRepository repo;

    public List<DocumentResponse> execute(Long employeeId) {

        return repo.findByEmployee_Id(employeeId)
                .stream()
                .map(doc -> {
                    DocumentResponse d = new DocumentResponse();
                    d.setId(doc.getId());
                    d.setFileName(doc.getFileName());
                    d.setFilePath(doc.getFilePath());
                    d.setFileType(doc.getFileType());
                    d.setCategory(doc.getCategory() != null ? doc.getCategory().name() : null);
                    d.setDocumentType(doc.getDocumentType());
                    d.setIsVerified(doc.getIsVerified());
                    d.setUploadedAt(doc.getUploadedAt());
                    d.setUploadedBy(doc.getUploadedBy());
                    d.setFileSize(getFileSize(doc.getFilePath()));
                    return d;
                }).collect(Collectors.toList());
    }

    private Long getFileSize(String filePath) {
        if (filePath == null) return 0L;
        try {
            File file = new File(filePath);
            return file.exists() ? file.length() : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}