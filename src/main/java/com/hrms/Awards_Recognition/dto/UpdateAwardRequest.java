package com.hrms.Awards_Recognition.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateAwardRequest {
    private String awardName;
    private LocalDate awardDate;
    private Long awardTypeId;
    private Long issuedById;
    private String description;
}