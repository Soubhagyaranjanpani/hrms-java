package com.hrms.master.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class SourceCategoryResponse {

    private  Long id;
    private  String name;
    private  Boolean active;
    private String status;
    private String createdBy;
    private LocalDateTime createAt;
}
