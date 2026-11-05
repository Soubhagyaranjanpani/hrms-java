package com.hrms.loan_advance.infrastructure;

import com.hrms.loan_advance.application.port.LoanAttachmentStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Keeps loan documents in a folder. Default folder: {upload.dir}/loan (upload.dir is already in your
 * application.properties); override with hrms.loan.attachment-dir.
 *
 * Stored key = random UUID + "_" + cleaned original name, so two uploads never overwrite each other and
 * a user-supplied name can never point outside the folder.
 */
@Component
public class LoanAttachmentLocalStorage implements LoanAttachmentStorage {

    private static final Pattern SAFE_KEY = Pattern.compile("^[A-Za-z0-9._-]{1,200}$");

    private final Path baseDirectory;

    public LoanAttachmentLocalStorage(
            @Value("${hrms.loan.attachment-dir:${upload.dir:./uploads}/loan}") String directory) {
        this.baseDirectory = Paths.get(directory).toAbsolutePath().normalize();
    }

    @Override
    public String store(String originalFileName, InputStream content) throws IOException {
        Files.createDirectories(baseDirectory);

        String cleanedName = originalFileName == null ? "file" : originalFileName.replaceAll("[^A-Za-z0-9._-]", "_");
        if (cleanedName.length() > 120) {
            cleanedName = cleanedName.substring(cleanedName.length() - 120);
        }
        String key = UUID.randomUUID() + "_" + cleanedName;

        Path target = baseDirectory.resolve(key).normalize();
        if (!target.startsWith(baseDirectory)) {
            throw new IOException("Invalid file name");
        }
        Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
        return key;
    }

    @Override
    public Optional<Resource> load(String storedKey) {
        Path file = resolve(storedKey);
        if (file == null || !Files.isRegularFile(file)) return Optional.empty();
        return Optional.of(new FileSystemResource(file));
    }

    @Override
    public boolean exists(String storedKey) {
        Path file = resolve(storedKey);
        return file != null && Files.isRegularFile(file);
    }

    /** null when the key is not one we could have produced. */
    private Path resolve(String storedKey) {
        if (storedKey == null || !SAFE_KEY.matcher(storedKey).matches() || storedKey.contains("..")) return null;
        Path file = baseDirectory.resolve(storedKey).normalize();
        return file.startsWith(baseDirectory) ? file : null;
    }
}