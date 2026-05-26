package com.hrms.payroll.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BulkGenerateRequest {
    private String  yearMonth;           // "YYYY-MM"
    private Integer workingDays;         // default 26
    private Boolean useSalaryStructure;  // true = use per-employee structure
    // fallback defaults if no structure set
    private Double  defaultBasic;
    private Double  defaultHra;
    private Double  defaultTravelAllow;
    private Double  defaultMedicalAllow;
    private Double  defaultSpecialAllow;
    private Double  defaultPF;
    private Double  defaultPT;
}
