package com.hrms.task.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED =
            Set.of("pdf", "png", "jpg", "jpeg", "doc", "docx", "xls", "xlsx", "txt", "zip");
    private static final long MAX_BYTES = 10L * 1024 * 1024;

    private final Path root;

    public FileStorageService(@Value("${upload.dir}") String uploadDir) {   // same property your other modules use
        this.root = Paths.get(uploadDir, "task-proofs").toAbsolutePath().normalize();
    }

    public record StoredFile(String originalName, String path) {}

    public StoredFile store(MultipartFile file, Long taskId) {
        String original = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "proof" : file.getOriginalFilename());
        if (original.contains("..")) throw bad("Invalid file name");

        String ext = original.contains(".")
                ? original.substring(original.lastIndexOf('.') + 1).toLowerCase() : "";
        if (!ALLOWED.contains(ext)) throw bad("File type not allowed. Allowed: " + ALLOWED);
        if (file.getSize() > MAX_BYTES) throw bad("File too large (max 10 MB)");

        try {
            Path dir = root.resolve(String.valueOf(taskId));
            Files.createDirectories(dir);
            String stored = UUID.randomUUID() + "." + ext;
            Files.copy(file.getInputStream(), dir.resolve(stored), StandardCopyOption.REPLACE_EXISTING);
            return new StoredFile(original, taskId + "/" + stored);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store proof file");
        }
    }

    public Resource load(String relativePath) {
        Path p = root.resolve(relativePath).normalize();
        if (!p.startsWith(root)) throw bad("Invalid path");
        try {
            Resource r = new UrlResource(p.toUri());
            if (!r.exists()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proof file not found");
            return r;
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proof file not found");
        }
    }

    private ResponseStatusException bad(String m) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, m);
    }
}
