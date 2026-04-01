package com.hrms.document.domain;

import com.hrms.document.domain.enums.DocumentCategory;
import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_documents")
@Data
public class EmployeeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    private String fileName;
    private String filePath;
    private String fileType; // PDF, IMAGE

    @Enumerated(EnumType.STRING)
    private DocumentCategory category; // SERVICE_BOOK, ID_PROOF

    private String extractedText; // OCR output

    private Boolean isVerified = false;

    private LocalDateTime uploadedAt;

    private String uploadedBy;
    private String documentType; // AUTO DETECTED
}
