package com.hrms.task.domain;



import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;

public enum TaskStatus {
    PENDING_APPROVAL("Pending Approval"),
    IN_PROGRESS("In Progress"),
    IN_REVIEW("In Review"),
    COMPLETED("Completed"),
    REJECTED("Rejected"),
    CHANGE_REQUESTED("Change Requested");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Valid transitions from each status
    private static final Map<TaskStatus, Set<TaskStatus>> VALID_TRANSITIONS = Map.of(
            PENDING_APPROVAL, Set.of(IN_PROGRESS, REJECTED, CHANGE_REQUESTED),
            IN_PROGRESS, Set.of(IN_REVIEW, COMPLETED),
            IN_REVIEW, Set.of(IN_PROGRESS, COMPLETED, CHANGE_REQUESTED),
            CHANGE_REQUESTED, Set.of(IN_PROGRESS, PENDING_APPROVAL),
            COMPLETED, Set.of(),
            REJECTED, Set.of()
    );

    public boolean canTransitionTo(TaskStatus targetStatus) {
        return VALID_TRANSITIONS.getOrDefault(this, Set.of()).contains(targetStatus);
    }

    public static TaskStatus fromString(String value) {
        if (value == null) return PENDING_APPROVAL;

        try {
            return TaskStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try to match by display name
            for (TaskStatus status : values()) {
                if (status.displayName.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            return PENDING_APPROVAL; // Default
        }
    }

    public static Set<TaskStatus> getActiveStatuses() {
        return Set.of(PENDING_APPROVAL, IN_PROGRESS, IN_REVIEW, CHANGE_REQUESTED);
    }

    public static Set<TaskStatus> getCompletedStatuses() {
        return Set.of(COMPLETED, REJECTED);
    }
}
