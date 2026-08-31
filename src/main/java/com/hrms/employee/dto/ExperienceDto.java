package com.hrms.employee.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExperienceDto {
    private String company;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
}