package com.hrms.Recuirment.dto;

import com.hrms.master.domain.SourceCategory;
import lombok.*;

@Data
//@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RecruitmentSourcesResponse {

    private  Long id;

    private  String sourceCode;

    private  String sourceName;

    private SourceCategory category;

    private String description;

    private  Integer displaySequence;

    private Boolean status;
}
