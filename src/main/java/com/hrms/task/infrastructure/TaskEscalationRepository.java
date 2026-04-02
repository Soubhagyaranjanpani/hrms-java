package com.hrms.task.infrastructure;

import com.hrms.task.domain.TaskEscalation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskEscalationRepository extends JpaRepository<TaskEscalation, Long> {}
