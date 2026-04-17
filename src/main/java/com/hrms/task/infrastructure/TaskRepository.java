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

//    List<Task> findByAssignedToAndIsDeletedFalse(Employee assignedTo);

    // used by GetAllTasksUseCase  (no parent = top-level tasks only)
    List<Task> findByIsDeletedFalseAndParentTaskIsNull();

    // used by GetTaskByIdUseCase to fetch subtasks
    List<Task> findByParentTaskAndIsDeletedFalse(Task parentTask);

    // used by SmartAssignUseCase
//    List<Task> findByAssignedToAndIsDeletedFalse(Employee emp);

    // used by TaskEscalationJob — overdue tasks
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

}
