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
public class NoticePeriodResponce {

    private Long id;
    private String periodCode;
    private String PeriodName;
    private Integer days;
    private String description;
    private String status;
    private String lastChangeBy;
    private LocalDateTime lastChangeAt;

}
