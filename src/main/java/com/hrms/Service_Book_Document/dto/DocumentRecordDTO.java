// path: src/main/java/com/hrms/Service_Book_Document/dto/DocumentRecordDTO.java
package com.hrms.Service_Book_Document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRecordDTO {
    private Long recordId;
    private String category;
    private String categoryLabel;
    private String title;
    private String referenceNo;
    private LocalDate eventDate;
    private String documentName;
    private String documentPath;
    private String fileType;
    private LocalDateTime uploadedAt;
}