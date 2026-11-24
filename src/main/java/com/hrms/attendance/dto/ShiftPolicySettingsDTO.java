package com.hrms.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftPolicySettingsDTO {

    @Builder.Default
    private Integer checkInBeforeMinutes = 5;

    @Builder.Default
    private Boolean enforceCheckOutAfterShift = true;

    @Builder.Default
    private Integer checkOutGraceMinutes = 10;

    private List<ShiftResponse> availableShifts;
}
