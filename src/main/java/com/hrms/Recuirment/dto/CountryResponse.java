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
public class CountryResponse {
    private Long id;
    private String countryCode;
    private String countryName;
    private String status;
    private String lastChangeBy;
    private LocalDateTime lastChangeAt;
}
