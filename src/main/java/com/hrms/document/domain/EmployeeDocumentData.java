package com.hrms.document.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class EmployeeDocumentData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private EmployeeDocument document;

    private String fieldName;   // e.g. "DOB"
    private String fieldValue;  // e.g. "01-01-1990"
}
