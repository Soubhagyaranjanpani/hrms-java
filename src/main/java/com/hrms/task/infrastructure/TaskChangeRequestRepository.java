package com.hrms.task.infrastructure;

import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskChangeRequestRepository extends JpaRepository<TaskChangeRequest, Long> {

    // get the latest pending change request for a task
    Optional<TaskChangeRequest> findTopByTaskAndStatusOrderByCreatedAtDesc(Task task, String status);
}
