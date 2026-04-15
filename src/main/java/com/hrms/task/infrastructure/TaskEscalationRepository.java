package com.hrms.task.infrastructure;

import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskEscalation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskEscalationRepository extends JpaRepository<TaskEscalation, Long> {
    List<TaskEscalation> findByTask(Task task);
}
