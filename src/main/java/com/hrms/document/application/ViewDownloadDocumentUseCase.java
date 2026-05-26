package com.hrms.document.application;



import com.hrms.document.domain.EmployeeDocument;
import com.hrms.document.infrastructure.EmployeeDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ViewDownloadDocumentUseCase {

    private final EmployeeDocumentRepository documentRepo;

    public ResponseEntity<Resource> execute(Long documentId, String mode) {

        try {
            // Get document from database
            EmployeeDocument doc = documentRepo.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));

            if (doc.getFilePath() == null) {
                return ResponseEntity.notFound().build();
            }

            // Load file as resource
            Path filePath = Paths.get(doc.getFilePath());

            // Check if file exists
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            Resource resource = new FileSystemResource(filePath);

            // Determine content type based on file extension
            String contentType = determineContentType(doc.getFileName());

            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            // Set Content-Disposition based on mode
            // V = View (inline), D = Download (attachment)
            if ("D".equalsIgnoreCase(mode)) {
                // Download mode - force download
                headers.setContentDispositionFormData("attachment", doc.getFileName());
            } else {
                // View mode - display inline (default)
                headers.setContentDispositionFormData("inline", doc.getFileName());
            }

            // Add cache control headers
            headers.setCacheControl("max-age=86400, public");

            // For PDFs, add additional headers
            if (contentType.equals("application/pdf")) {
                headers.add("X-Content-Type-Options", "nosniff");

                // For inline PDF viewing
                if (!"D".equalsIgnoreCase(mode)) {
                    headers.add("Content-Transfer-Encoding", "binary");
                }
            }

            // For images, add proper headers
            if (contentType.startsWith("image/")) {
                headers.setCacheControl("max-age=31536000, public, immutable");
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String determineContentType(String fileName) {
        if (fileName == null) return "application/octet-stream";

        String fileNameLower = fileName.toLowerCase();

        if (fileNameLower.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileNameLower.endsWith(".jpg") || fileNameLower.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileNameLower.endsWith(".png")) {
            return "image/png";
        } else if (fileNameLower.endsWith(".gif")) {
            return "image/gif";
        } else if (fileNameLower.endsWith(".bmp")) {
            return "image/bmp";
        } else if (fileNameLower.endsWith(".webp")) {
            return "image/webp";
        } else if (fileNameLower.endsWith(".doc")) {
            return "application/msword";
        } else if (fileNameLower.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (fileNameLower.endsWith(".xls")) {
            return "application/vnd.ms-excel";
        } else if (fileNameLower.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (fileNameLower.endsWith(".ppt")) {
            return "application/vnd.ms-powerpoint";
        } else if (fileNameLower.endsWith(".pptx")) {
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        } else if (fileNameLower.endsWith(".txt")) {
            return "text/plain";
        } else if (fileNameLower.endsWith(".csv")) {
            return "text/csv";
        } else if (fileNameLower.endsWith(".json")) {
            return "application/json";
        } else if (fileNameLower.endsWith(".xml")) {
            return "application/xml";
        } else if (fileNameLower.endsWith(".zip")) {
            return "application/zip";
        }

        return "application/octet-stream";
    }
}
