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
public class InterviewRoundResponse {
    private Long id;
    private String roundCode;
    private String roundName;
    private String roundSequence;
    private String mandatory;
    private String maximumScore;
    private String passingScore;
    private String status;
    private LocalDateTime lastChangeAt;
    private String lastChangeBy;
    private Long departmentId;
   //

}
