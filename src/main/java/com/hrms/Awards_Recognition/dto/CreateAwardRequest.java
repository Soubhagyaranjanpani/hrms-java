package com.hrms.Awards_Recognition.dto;


import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class CreateAwardRequest {
    private Long employeeId;
    private String awardName;
    private LocalDate awardDate;
    private Long awardTypeId;        // ✅ Dropdown 1 - From award_types table
    private Long issuedById;         // ✅ Dropdown 2 - From employee_designation table
    private String description;
}