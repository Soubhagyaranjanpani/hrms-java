package com.hrms.document.domain.enums;

public class individualEmployeeDocument {

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

    @Entity
    @Table(name = "service_book_documents")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class Document {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String fileName;

        @Column(nullable = false)
        private String filePath; // Path on server disk or cloud URL

        private Long fileSize; // In bytes

        private String documentType; // PDF, DOCX, JPG, PNG


    }





}
