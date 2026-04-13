package com.hrms.document.application;

import com.hrms.document.domain.*;
import com.hrms.document.domain.enums.DocumentCategory;
import com.hrms.document.infrastructure.EmployeeDocumentRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.audit.application.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class UploadDocumentUseCase {

    private final EmployeeRepository employeeRepo;
    private final EmployeeDocumentRepository documentRepo;
    private final AuditLogService audit;

    @Value("${upload.dir}")
    private String uploadDir;

    public String execute(Long employeeId,
                          String category,
                          MultipartFile file,
                          String user) {

        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        try {
            // Create uploads directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = System.currentTimeMillis() + "_" + employeeId + fileExtension;

            // Determine file type category
            String fileType = determineFileType(fileExtension);
            String documentType = fileExtension.length() > 1 ? fileExtension.substring(1).toUpperCase() : "UNKNOWN";

            // Save file
            String path = uploadDir + File.separator + uniqueFilename;
            File dest = new File(path);
            file.transferTo(dest);
            System.out.println("File saved to: " + dest.getAbsolutePath());

            // Create and populate EmployeeDocument entity
            EmployeeDocument doc = new EmployeeDocument();
            doc.setEmployee(emp);
            doc.setFileName(originalFilename);
            doc.setFilePath(path);
            doc.setFileType(fileType);
            doc.setDocumentType(documentType);

            // Set category
            try {
                doc.setCategory(DocumentCategory.valueOf(category.toUpperCase()));
            } catch (IllegalArgumentException e) {
                doc.setCategory(DocumentCategory.OTHER);
            }

            doc.setUploadedAt(LocalDateTime.now());
            doc.setUploadedBy(emp.getFirstName());
            doc.setIsVerified(false);
            doc.setExtractedText(null);

            // Save to database
            EmployeeDocument savedDoc = documentRepo.save(doc);
            System.out.println("Document saved to database with ID: " + savedDoc.getId());

            // Audit log
            audit.log("DOCUMENT", savedDoc.getId(), "DOCUMENT_UPLOADED", user, null, savedDoc);

            return "Document uploaded successfully";

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }

    private String determineFileType(String extension) {
        if (extension == null) return "OTHER";

        String ext = extension.toLowerCase();
        if (ext.equals(".pdf")) return "PDF";
        if (ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") || ext.equals(".gif")) return "IMAGE";
        if (ext.equals(".doc") || ext.equals(".docx")) return "DOCUMENT";
        if (ext.equals(".xls") || ext.equals(".xlsx")) return "SPREADSHEET";

        return "OTHER";
    }
}