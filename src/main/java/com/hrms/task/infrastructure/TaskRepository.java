package com.hrms.task.infrastructure;

import com.hrms.employee.domain.Employee;
import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedToAndIsDeletedFalse(Employee employee);

    List<Task> findByDepartment_IdAndIsDeletedFalse(Long departmentId);

    List<Task> findByIsDeletedFalseAndParentTaskIsNull();

    List<Task> findByParentTaskAndIsDeletedFalse(Task parentTask);

    @Query("SELECT t FROM Task t WHERE t.dueDate IS NOT NULL " +
            "AND t.dueDate < CURRENT_TIMESTAMP " +
            "AND t.status NOT IN ('COMPLETED','REJECTED') " +
            "AND t.isDeleted = false")
    List<Task> findOverdueTasks();

    Long countByAssignedToIdAndIsDeletedFalse(Long employeeId);

    Long countByAssignedToIdAndStatusAndIsDeletedFalse(Long employeeId, TaskStatus status);

    Long countByAssignedToIdAndDueDateBeforeAndStatusNotAndIsDeletedFalse(
            Long employeeId,
            java.time.LocalDateTime now,
            TaskStatus status
    );

    // ─── FIXED: removed MONTH(updatedAt) filter that was causing 0 counts ───
    // Also removed CURRENT_DATE comparison with LocalDateTime dueDate (type mismatch)
    @Query("""
        SELECT
            COUNT(t),
            SUM(CASE WHEN t.status = 'PENDING_APPROVAL' THEN 1 ELSE 0 END),
            SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END),
            SUM(CASE WHEN t.status = 'DRAFT' THEN 1 ELSE 0 END),
            SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END),
            SUM(CASE WHEN t.status = 'IN_REVIEW' THEN 1 ELSE 0 END),
            SUM(CASE WHEN t.dueDate IS NOT NULL
                      AND t.dueDate < CURRENT_TIMESTAMP
                      AND t.status NOT IN ('COMPLETED','REJECTED') THEN 1 ELSE 0 END)
        FROM Task t WHERE t.isDeleted = false AND t.parentTask IS NULL
    """)
    Object[] aggregateStats();

    // Recent 5 top-level tasks
    List<Task> findTop5ByIsDeletedFalseOrderByCreatedAtDesc();

    // ─── NEW: needed by dashboard fallback logic ───────────────────────────
    long countByIsDeletedFalseAndParentTaskIsNull();

    // Return typed Task list so callers can access Task-specific getters
    List<Task> findByIsDeletedFalse();
}