package com.hrms.employee.application;

import com.hrms.employee.domain.EmployeeConfirmationDocument;
import com.hrms.employee.infrastructure.EmployeeConfirmationDocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeConfirmationDocumentService {

    private final EmployeeConfirmationDocumentRepository repository;

    @Value("${upload.dir}")
    private String uploadDir;

    public EmployeeConfirmationDocumentService(EmployeeConfirmationDocumentRepository repository) {
        this.repository = repository;
    }

    // ================= Upload File =================
    public EmployeeConfirmationDocument uploadFile(Long confirmationId,
                                                   MultipartFile file) throws IOException {

        Files.createDirectories(Paths.get(uploadDir));

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new RuntimeException("Invalid file name");
        }

        String cleanFileName = Paths.get(originalFileName).getFileName().toString();
        String extension = cleanFileName.substring(cleanFileName.lastIndexOf("."));

        String storedFileName = UUID.randomUUID() + extension;

        Path filePath = Paths.get(uploadDir, storedFileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        EmployeeConfirmationDocument doc = new EmployeeConfirmationDocument();
        doc.setEmployeeConfirmationId(confirmationId);
        doc.setFileName(cleanFileName);
        doc.setFilePath(filePath.toString());
        doc.setFileType(file.getContentType());
        doc.setIsDeleted(false);

        return repository.save(doc);
    }

    // ================= Update File =================
    public EmployeeConfirmationDocument updateFile(Long id,
                                                   Long confirmationId,
                                                   MultipartFile file) throws IOException {

        EmployeeConfirmationDocument document = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Delete old file
        if (document.getFilePath() != null) {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
        }

        Files.createDirectories(Paths.get(uploadDir));

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new RuntimeException("Invalid file name");
        }

        String cleanFileName = Paths.get(originalFileName).getFileName().toString();
        String extension = cleanFileName.substring(cleanFileName.lastIndexOf("."));

        String storedFileName = UUID.randomUUID() + extension;

        Path filePath = Paths.get(uploadDir, storedFileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        document.setEmployeeConfirmationId(confirmationId);
        document.setFileName(cleanFileName);
        document.setFilePath(filePath.toString());
        document.setFileType(file.getContentType());
        document.setIsDeleted(false);

        return repository.save(document);
    }

    // ================= Get All =================
    public List<EmployeeConfirmationDocument> getAllByFlag(int flag) {

        if (flag == 0) {
            return repository.findByIsDeleted(false);
        }

        return repository.findByIsDeleted(true);
    }

    // ================= Get By Confirmation Id =================
    public List<EmployeeConfirmationDocument> getByConfirmationId(Long confirmationId) {
        return repository.findByEmployeeConfirmationId(confirmationId);
    }

    // ================= Soft Delete =================
    public void delete(Long id) {

        EmployeeConfirmationDocument document = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setIsDeleted(true);

        repository.save(document);
    }
}