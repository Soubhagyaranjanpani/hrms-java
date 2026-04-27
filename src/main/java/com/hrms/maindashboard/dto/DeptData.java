package com.hrms.maindashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public  class DeptData {
    private String department;
    private int count;
    private double pct;
}
