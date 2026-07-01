package com.hrms.employee.api;

import com.hrms.employee.application.EmployeeConfirmationDocumentService;
import com.hrms.employee.domain.EmployeeConfirmationDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/confirmation-documents")
public class EmployeeConfirmationDocumentController {

    private final EmployeeConfirmationDocumentService service;

    public EmployeeConfirmationDocumentController(EmployeeConfirmationDocumentService service) {
        this.service = service;
    }

    // Upload File
    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam Long confirmationId,
            @RequestPart("file") MultipartFile file) {

        try {
            EmployeeConfirmationDocument document = service.uploadFile(confirmationId, file);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam Long confirmationId,
            @RequestPart("file") MultipartFile file) {

        System.out.println("Update API Hit");
        System.out.println("Id = " + id);
        System.out.println("Confirmation Id = " + confirmationId);

        try {
            return ResponseEntity.ok(service.updateFile(id, confirmationId, file));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    // Get All
    // flag = 0 -> Active Records
    // flag = 1 -> Deleted Records
    @GetMapping("/all/{flag}")
    public ResponseEntity<List<EmployeeConfirmationDocument>> getAll(
            @PathVariable int flag) {

        return ResponseEntity.ok(service.getAllByFlag(flag));
    }


    @GetMapping("/confirmation/{confirmationId}")
    public ResponseEntity<List<EmployeeConfirmationDocument>> getByConfirmationId(
            @PathVariable Long confirmationId) {

        return ResponseEntity.ok(service.getByConfirmationId(confirmationId));
    }

    // Soft Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.ok("Document deleted successfully.");
    }

}