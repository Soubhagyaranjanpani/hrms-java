package com.hrms.task.domain;

public enum RoleType {
    ADMIN, HR, MANAGER, EMPLOYEE;

    /** Accepts "ADMIN", "ROLE_ADMIN", "admin", an enum, etc. Unknown values become EMPLOYEE. */
    public static RoleType from(Object raw) {
        if (raw == null) return EMPLOYEE;
        String v = String.valueOf(raw).trim().toUpperCase().replace("ROLE_", "");
        return switch (v) {
            case "ADMIN", "SUPER_ADMIN" -> ADMIN;   // your DB value "Admin" works (upper-cased)
            case "HR" -> HR;
            case "MANAGER" -> MANAGER;
            default -> EMPLOYEE;
        };
    }

    public boolean isPrivileged() {
        return this == ADMIN || this == HR;
    }
}
