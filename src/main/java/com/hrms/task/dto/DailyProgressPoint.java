package com.hrms.task.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyProgressPoint {
    private LocalDate date;
    private Integer submissions;
    private Integer approved;
    private Double hoursSpent;
    private Double avgProgress;   // average progress % submitted that day
}
