package com.hrms.Awards_Recognition.dto;

import com.hrms.master.dto.AwardTypeResponse;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AwardRecordResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String departmentName;
    private String designationName;
    private String awardName;
    private LocalDate awardDate;

    // ✅ Dropdown 1 - Full Award Type object
    private AwardTypeResponse awardType;

    // ✅ Dropdown 2 - Issued By (simplified)
    private Long issuedById;
    private String issuedByName;
    private String issuedByDesignation;

    private String description;
    private Boolean isActive;
    private String documentPath;
    private String documentName;
    private String processedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}