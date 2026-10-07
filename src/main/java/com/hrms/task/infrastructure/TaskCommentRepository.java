package com.hrms.task.infrastructure;

import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    List<TaskComment> findByTask_Id(Long taskId);
    List<TaskComment> findByTaskOrderByCreatedAtAsc(Task task);
}
