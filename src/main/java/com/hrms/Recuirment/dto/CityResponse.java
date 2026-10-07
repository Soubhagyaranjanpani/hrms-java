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
public class CityResponse {
    private Long id;
    private String cityCode;
    private String cityName;
    private String status;
    private String lastChangeBy;
    private LocalDateTime lastChangeAt;
    private Long stateId;
}
