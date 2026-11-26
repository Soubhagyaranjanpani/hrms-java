package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.task.domain.*;
import com.hrms.task.dto.ReviewSubmissionRequest;
import com.hrms.task.dto.SubmissionResponse;
import com.hrms.task.infrastructure.TaskRepository;
import com.hrms.task.infrastructure.TaskSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Manager (or the assigner / Admin / HR) approves or rejects a submission. */
@Service
@RequiredArgsConstructor
public class ReviewSubmissionUseCase {

    private final TaskSubmissionRepository submissionRepo;
    private final TaskRepository taskRepo;
    private final AccessScopeService scope;
    private final GetTaskSubmissionsUseCase mapper;

    @Transactional
    public SubmissionResponse execute(Long submissionId, ReviewSubmissionRequest req, String email) {

        TaskSubmission s = submissionRepo.findById(submissionId)
                .orElseThrow(() -> scope.badRequest("Submission not found: " + submissionId));
        Task task = s.getTask();
        Employee reviewer = scope.currentUser(email);

        if (!scope.canReviewTask(reviewer, task))
            throw scope.forbidden("You are not allowed to review this submission");
        if (s.getStatus() != SubmissionStatus.SUBMITTED)
            throw scope.badRequest("Submission already " + s.getStatus());
        if (req.getApproved() == null)
            throw scope.badRequest("'approved' is required");

        boolean approve = req.getApproved();
        if (!approve && !StringUtils.hasText(req.getComment()))
            throw scope.badRequest("Comment is required when rejecting");

        LocalDateTime now = LocalDateTime.now();
        s.setStatus(approve ? SubmissionStatus.APPROVED : SubmissionStatus.REJECTED);
        s.setReviewedBy(reviewer);
        s.setReviewComment(req.getComment());
        s.setReviewedAt(now);
        submissionRepo.save(s);

        List<TaskSubmission> all = submissionRepo.findByTaskOrderBySubmittedAtDesc(task);

        if (Boolean.TRUE.equals(s.getFinalSubmission()) && task.getStatus() == TaskStatus.IN_REVIEW) {
            if (approve) {
                task.setStatus(TaskStatus.COMPLETED);
                task.setProgress(100);
                double hours = all.stream()
                        .filter(x -> x.getStatus() != SubmissionStatus.REJECTED)
                        .map(TaskSubmission::getHoursSpent).filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue).sum();
                task.setActualHours((int) Math.round(hours));
            } else {
                task.setStatus(TaskStatus.IN_PROGRESS);   // send back to employee
            }
        }

        if (!approve) {
            // progress = best non-rejected submission so far
            task.setProgress(all.stream()
                    .filter(x -> x.getStatus() != SubmissionStatus.REJECTED)
                    .map(TaskSubmission::getProgressPercent).filter(Objects::nonNull)
                    .max(Integer::compare).orElse(0));
        }

        task.setUpdatedAt(now);
        taskRepo.save(task);

        return mapper.map(s);
    }
}
