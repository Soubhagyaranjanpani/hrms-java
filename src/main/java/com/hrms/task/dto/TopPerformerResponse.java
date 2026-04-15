package com.hrms.task.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopPerformerResponse {

    private Long employeeId;
    private String name;
    private String department;
    private Double rating;
    private String improvementPercent;
}
