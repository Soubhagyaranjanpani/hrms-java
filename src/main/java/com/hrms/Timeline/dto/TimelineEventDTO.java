package com.hrms.Timeline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineEventDTO {
    private Long id;
    private String type;          // appointment, confirmation, promotion, transfer,
    // deputation, payRevision, disciplinary, award,
    // training, retirement
    private String title;
    private String description;
    private LocalDate date;
    private String referenceNo;
    private String sourceModule;
    private String approvedBy;
    private String remarks;
    private String department;
    private String designation;
}