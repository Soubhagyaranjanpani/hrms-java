package com.hrms.task.infrastructure;

import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskComment;
import com.hrms.task.domain.TaskDependency;
import com.hrms.task.domain.TaskEscalation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {

    List<TaskDependency> findByTask(Task task);
}

