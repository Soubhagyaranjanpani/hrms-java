package com.hrms.appointment_type.api;

import com.hrms.appointment_type.domain.UploadFile;
import com.hrms.appointment_type.infrastructure.UploadFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final UploadFileRepository repository;

    @Value("${upload.dir}")
    private String uploadDir;

    public UploadController(UploadFileRepository repository) {
        this.repository = repository;
    }

    // =========================
    // 1. UPLOAD API
    // =========================
    @PostMapping("/upload")
    public String uploadFile(
            @RequestParam(required = false) String name,
            @RequestParam MultipartFile image) throws Exception {

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalName = image.getOriginalFilename();
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String safeFileName = UUID.randomUUID().toString() + extension;
        String filePath = uploadDir + File.separator + safeFileName;

        image.transferTo(new File(filePath));

        UploadFile file = new UploadFile();
        file.setName(name != null ? name : "Uploaded File");
        file.setFilePath(filePath);

        repository.save(file);

        return "File uploaded successfully: " + safeFileName;
    }

    // =========================
    // 2. VIEW ALL API
    // =========================
    @GetMapping("/files")
    public List<UploadFile> getAllFiles() {
        return repository.findAll();
    }

    // =========================
    // 3. VIEW BY ID API
    // =========================
    @GetMapping("/files/{id}")
    public UploadFile getFileById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    // =========================
    // 4. UPDATE API
    // =========================
    @PutMapping("/files/{id}")
    public UploadFile updateFile(
            @PathVariable Long id,
            @RequestBody UploadFile newFile) {

        UploadFile existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        existing.setName(newFile.getName());
        existing.setFilePath(newFile.getFilePath());

        return repository.save(existing);
    }

    // =========================
    // 5. DOWNLOAD API (WITH VIEW LOGIC)
    // =========================
    @GetMapping("/files/download/{id}")
    public byte[] downloadFile(@PathVariable Long id) throws Exception {

        UploadFile file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        File f = new File(file.getFilePath());

        return java.nio.file.Files.readAllBytes(f.toPath());
    }
}