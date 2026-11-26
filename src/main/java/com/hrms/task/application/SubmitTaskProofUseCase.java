package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.task.domain.*;
import com.hrms.task.dto.SubmissionResponse;
import com.hrms.task.dto.SubmitProofRequest;
import com.hrms.task.infrastructure.TaskRepository;
import com.hrms.task.infrastructure.TaskSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/** Employee submits daily progress / final work with proof. */
@Service
@RequiredArgsConstructor
public class SubmitTaskProofUseCase {

    private final TaskRepository taskRepo;
    private final TaskSubmissionRepository submissionRepo;
    private final AccessScopeService scope;
    private final FileStorageService storage;
    private final GetTaskSubmissionsUseCase mapper;

    @Transactional
    public SubmissionResponse execute(Long taskId, SubmitProofRequest req, String email) {

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> scope.badRequest("Task not found with ID: " + taskId));
        if (Boolean.TRUE.equals(task.getIsDeleted())) throw scope.badRequest("Task is deleted");

        Employee emp = scope.currentUser(email);

        if (!scope.isSame(emp, task.getAssignedTo()))
            throw scope.forbidden("Only the assignee can submit work for this task");

        if (task.getStatus() != TaskStatus.IN_PROGRESS)
            throw scope.badRequest("Task must be In Progress to submit work. Current status: "
                    + task.getStatus().getDisplayName());

        LocalDateTime now = LocalDateTime.now();
        if (task.getStartDate() != null && now.isBefore(task.getStartDate()))
            throw scope.badRequest("Task has not started yet");

        Integer progress = req.getProgress();
        if (progress == null || progress < 0 || progress > 100)
            throw scope.badRequest("Progress must be between 0 and 100");

        boolean isFinal = Boolean.TRUE.equals(req.getFinalSubmission());
        boolean hasFile = req.getFile() != null && !req.getFile().isEmpty();
        boolean hasLink = StringUtils.hasText(req.getProofLink());

        if (isFinal && !hasFile && !hasLink)
            throw scope.badRequest("Final submission needs proof (file or link)");
        if (!hasFile && !hasLink && !StringUtils.hasText(req.getNote()))
            throw scope.badRequest("Add a note or proof");
        if (req.getHoursSpent() != null && (req.getHoursSpent() < 0 || req.getHoursSpent() > 24))
            throw scope.badRequest("Hours spent must be between 0 and 24");

        if (isFinal) progress = 100;

        TaskSubmission s = new TaskSubmission();
        s.setTask(task);
        s.setSubmittedBy(emp);
        s.setNote(req.getNote());
        s.setProofLink(hasLink ? req.getProofLink().trim() : null);
        s.setProgressPercent(progress);
        s.setHoursSpent(req.getHoursSpent());
        s.setFinalSubmission(isFinal);
        s.setLate(task.getDueDate() != null && now.isAfter(task.getDueDate()));
        s.setSubmittedAt(now);
        s.setSubmissionDate(now.toLocalDate());
        s.setStatus(SubmissionStatus.SUBMITTED);

        if (hasFile) {
            FileStorageService.StoredFile f = storage.store(req.getFile(), taskId);
            s.setProofFileName(f.originalName());
            s.setProofFilePath(f.path());
        }

        submissionRepo.save(s);

        // reflect progress on the task; final submission sends it to review
        task.setProgress(Math.max(task.getProgress() == null ? 0 : task.getProgress(), progress));
        if (isFinal) task.setStatus(TaskStatus.IN_REVIEW);
        task.setUpdatedAt(now);
        taskRepo.save(task);

        return mapper.map(s);
    }
}
