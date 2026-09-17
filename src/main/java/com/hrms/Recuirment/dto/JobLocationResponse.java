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
public class JobLocationResponse {
    private Long id;
    private String locationCode;
    private String locationName;
    private String pinCode;
    private String description;
    private String status;
    private String lastChangeBy;
    private LocalDateTime lastChangeAt;
    private Long branchId;
    private Long countryId;
    private Long stateId;
    private Long cityId;
    private Long workModeId;

}
