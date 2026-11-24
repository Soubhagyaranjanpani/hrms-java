package com.hrms.attendance.domain;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public enum ShiftType {

    GS("GS", "General Shift", LocalTime.of(9, 0), LocalTime.of(18, 0), 9.0, 60, 15, false, "Corporate General Day Shift (9:00 AM – 6:00 PM)"),
    MS("MS", "Morning Shift", LocalTime.of(6, 0), LocalTime.of(14, 0), 8.0, 45, 15, false, "Early Operational Shift (6:00 AM – 2:00 PM)"),
    DS("DS", "Day Shift", LocalTime.of(14, 0), LocalTime.of(22, 0), 8.0, 45, 15, false, "Afternoon / Day Operational Shift (2:00 PM – 10:00 PM)"),
    NS("NS", "Night Shift", LocalTime.of(22, 0), LocalTime.of(6, 0), 8.0, 45, 15, true, "Overnight Operational Shift (10:00 PM – 6:00 AM)"),
    S39("S39", "3-9 Shift", LocalTime.of(15, 0), LocalTime.of(21, 0), 6.0, 30, 10, false, "Special Operational Shift (3:00 PM – 9:00 PM)"),
    S129("12-9", "12-9 Shift", LocalTime.of(12, 0), LocalTime.of(9, 0), 21.0, 45, 10, true, "12-9 Shift (12:00 PM – 9:00 AM)");

    private final String code;
    private final String name;
    private final LocalTime shiftStart;
    private final LocalTime shiftEnd;
    private final Double durationHours;
    private final Integer breakMinutes;
    private final Integer graceMinutes;
    private final boolean isNightShift;
    private final String description;

    ShiftType(String code, String name, LocalTime shiftStart, LocalTime shiftEnd,
              Double durationHours, Integer breakMinutes, Integer graceMinutes,
              boolean isNightShift, String description) {
        this.code = code;
        this.name = name;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.durationHours = durationHours;
        this.breakMinutes = breakMinutes;
        this.graceMinutes = graceMinutes;
        this.isNightShift = isNightShift;
        this.description = description;
    }

    public LocalTime getCheckInAllowedTime(int checkInBeforeMinutes) {
        return shiftStart.minusMinutes(checkInBeforeMinutes);
    }

    public static ShiftType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return GS;
        }
        String clean = code.trim().toUpperCase().replace("-", "").replace("_", "");
        for (ShiftType type : values()) {
            if (type.getCode().equalsIgnoreCase(code.trim()) || type.name().equalsIgnoreCase(code.trim())) {
                return type;
            }
        }
        if (clean.equals("G") || clean.equals("GENERAL") || clean.equals("GENERALSHIFT")) return GS;
        if (clean.equals("M") || clean.equals("MORNING") || clean.equals("MORNINGSHIFT")) return MS;
        if (clean.equals("D") || clean.equals("DAY") || clean.equals("DAYSHIFT")) return DS;
        if (clean.equals("N") || clean.equals("NIGHT") || clean.equals("NIGHTSHIFT")) return NS;
        if (clean.equals("39") || clean.equals("S39") || clean.equals("3TO9")) return S39;
        if (clean.equals("129") || clean.equals("12-9") || clean.equals("S129") || clean.equals("12TO9")) return S129;

        return GS;
    }
}
