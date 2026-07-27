package com.hrms.payrevision.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class PayRevisionDocumentStorageService {

    @Value("${upload.dir}")
    private String uploadDir;

    private static final String SUBFOLDER = "pay-revisions";

    /** Saves generated PDF bytes to disk and returns the absolute path. */
    public String saveGenerated(Long recordId, String employeeCode, byte[] pdfBytes) throws IOException {
        Path dir = Paths.get(uploadDir, SUBFOLDER);
        Files.createDirectories(dir);

        String safeCode = sanitize(employeeCode);
        String fileName = "PayRevisionLetter_" + safeCode + "_" + recordId + ".pdf";
        Path target = dir.resolve(fileName);

        Files.write(target, pdfBytes);
        return target.toString();
    }

    /** Saves a manually uploaded file (overrides the auto-generated one) and returns the absolute path. */
    public String saveUploaded(Long recordId, MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadDir, SUBFOLDER);
        Files.createDirectories(dir);

        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        String fileName = "PayRevisionDoc_" + recordId + "_" + System.currentTimeMillis() + ext;
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
