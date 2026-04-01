package com.hrms.document.application;

import com.hrms.document.domain.*;
import com.hrms.document.domain.enums.DocumentCategory;
import com.hrms.document.infrastructure.EmployeeDocumentRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.audit.application.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UploadDocumentUseCase {

    private final EmployeeRepository employeeRepo;
    private final EmployeeDocumentRepository documentRepo;
    private final AuditLogService audit;

    public String execute(Long employeeId,
                          String category,
                          MultipartFile file,
                          String user) {

        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        try {
            // 🔥 Save file locally (you can replace with S3 later)
            String path = "uploads/" + file.getOriginalFilename();
            File dest = new File(path);
            file.transferTo(dest);

            EmployeeDocument doc = new EmployeeDocument();
            doc.setEmployee(emp);
            doc.setFileName(file.getOriginalFilename());
            doc.setFilePath(path);
            doc.setFileType(file.getContentType());
            doc.setCategory(DocumentCategory.valueOf(category));
            doc.setUploadedAt(LocalDateTime.now());
            doc.setUploadedBy(user);

            documentRepo.save(doc);

            audit.log("DOCUMENT", doc.getId(), "DOCUMENT_UPLOADED", user, null, doc);

            return "Document uploaded successfully";

        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
