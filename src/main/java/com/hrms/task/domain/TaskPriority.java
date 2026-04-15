package com.hrms.task.domain;



public enum TaskPriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TaskPriority fromString(String value) {
        if (value == null) return MEDIUM;

        try {
            return TaskPriority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try to match by display name
            for (TaskPriority priority : values()) {
                if (priority.displayName.equalsIgnoreCase(value)) {
                    return priority;
                }
            }
            return MEDIUM; // Default
        }
    }
}
