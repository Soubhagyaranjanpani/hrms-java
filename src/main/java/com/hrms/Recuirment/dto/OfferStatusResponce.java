package com.hrms.Recuirment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferStatusResponce {
    private Long id;
    private String statusCode;
    private String statusName;
    private String description;
    private String status;
    private String lastChangeBy;
    private LocalDateTime lastchangeAt;
}
