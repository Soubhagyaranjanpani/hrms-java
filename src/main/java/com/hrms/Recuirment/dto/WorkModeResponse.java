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
public class WorkModeResponse {
    private Long id;
    private String workModeName;
    private String status;
    private String lastChangeBy;
    private LocalDateTime lastChangeAt;
}
