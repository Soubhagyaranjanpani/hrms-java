package com.hrms.master.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceCategoryResponse {

    private  Long id;
    private  String name;
    private  Boolean active;
    private String createdBy;
    private  String updatedBy;
}
