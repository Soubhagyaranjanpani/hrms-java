package com.hrms.payroll.dto;

import lombok.Data;

import java.util.List;
@Data
public class SubmitRequest {
    private List<Long> recordIds;
}
