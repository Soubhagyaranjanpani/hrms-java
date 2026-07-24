package com.hrms.confirmation.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ConfirmationDocumentStorageService {

    @Value("${upload.dir}")
    private String uploadDir;

    private static final String SUBFOLDER = "confirmations";

    /** Saves generated PDF bytes to disk and returns the absolute path. */
    public String saveGenerated(Long confirmationId, String employeeCode, byte[] pdfBytes) throws IOException {
        Path dir = Paths.get(uploadDir, SUBFOLDER);
        Files.createDirectories(dir);

        String safeCode = sanitize(employeeCode);
        String fileName = "ConfirmationLetter_" + safeCode + "_" + confirmationId + ".pdf";
        Path target = dir.resolve(fileName);

        Files.write(target, pdfBytes);
        return target.toString();
    }

    /** Saves a manually uploaded file (overrides the auto-generated one) and returns the absolute path. */
    public String saveUploaded(Long confirmationId, MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadDir, SUBFOLDER);
        Files.createDirectories(dir);

        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        String fileName = "ConfirmationDoc_" + confirmationId + "_" + System.currentTimeMillis() + ext;
        Path target = dir.resolve(fileName);

        try (var in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return target.toString();
    }

    public byte[] readFile(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public String fileNameOf(String path) {
        return Paths.get(path).getFileName().toString();
    }

    private String sanitize(String s) {
        if (s == null) return "unknown";
        return s.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
