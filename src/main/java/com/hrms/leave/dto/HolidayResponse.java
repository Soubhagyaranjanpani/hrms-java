package com.hrms.leave.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Holiday details")
public class HolidayResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Republic Day")
    private String name;

    @Schema(example = "2026-01-26")
    private LocalDate date;

    @Schema(example = "true")
    private Boolean isActive;
}
