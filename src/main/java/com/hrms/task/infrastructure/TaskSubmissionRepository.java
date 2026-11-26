package com.hrms.task.infrastructure;

import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long> {

    List<TaskSubmission> findByTaskOrderBySubmittedAtDesc(Task task);

    List<TaskSubmission> findBySubmittedByIdAndSubmissionDateBetween(
            Long employeeId, LocalDate from, LocalDate to);

    List<TaskSubmission> findBySubmittedByIdInAndSubmissionDateBetween(
            Collection<Long> employeeIds, LocalDate from, LocalDate to);
}
