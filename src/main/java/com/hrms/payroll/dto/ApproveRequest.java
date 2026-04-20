package com.hrms.payroll.dto;

import lombok.Data;

import java.util.List;

@Data
public  class ApproveRequest {
    private String     yearMonth;
    private List<Long> recordIds;
}
