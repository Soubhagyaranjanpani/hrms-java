package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskSubmission;
import com.hrms.task.dto.SubmissionResponse;
import com.hrms.task.infrastructure.TaskRepository;
import com.hrms.task.infrastructure.TaskSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTaskSubmissionsUseCase {

    private final TaskRepository taskRepo;
    private final TaskSubmissionRepository submissionRepo;
    private final AccessScopeService scope;
    private final FileStorageService storage;

    public record ProofFile(Resource resource, String filename) {}

    public List<SubmissionResponse> execute(Long taskId, String email) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> scope.badRequest("Task not found with ID: " + taskId));
        scope.assertCanViewTask(scope.currentUser(email), task);
        return submissionRepo.findByTaskOrderBySubmittedAtDesc(task).stream().map(this::map).toList();
    }

    public ProofFile proof(Long submissionId, String email) {
        TaskSubmission s = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));
        scope.assertCanViewTask(scope.currentUser(email), s.getTask());
        if (s.getProofFilePath() == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No proof file on this submission");
        return new ProofFile(storage.load(s.getProofFilePath()), s.getProofFileName());
    }

    SubmissionResponse map(TaskSubmission s) {
        SubmissionResponse r = new SubmissionResponse();
        r.setId(s.getId());
        r.setTaskId(s.getTask().getId());
        r.setTaskTitle(s.getTask().getTitle());
        Employee e = s.getSubmittedBy();
        r.setEmployeeId(e.getId());
        r.setEmployeeName(e.getFirstName() + " " + e.getLastName());
        r.setNote(s.getNote());
        r.setProofLink(s.getProofLink());
        r.setProofFileName(s.getProofFileName());
        r.setHasProofFile(s.getProofFilePath() != null);
        r.setProgressPercent(s.getProgressPercent());
        r.setHoursSpent(s.getHoursSpent());
        r.setFinalSubmission(s.getFinalSubmission());
        r.setLate(s.getLate());
        r.setSubmissionDate(s.getSubmissionDate());
        r.setSubmittedAt(s.getSubmittedAt());
        r.setStatus(s.getStatus().name());
        if (s.getReviewedBy() != null)
            r.setReviewedBy(s.getReviewedBy().getFirstName() + " " + s.getReviewedBy().getLastName());
        r.setReviewComment(s.getReviewComment());
        r.setReviewedAt(s.getReviewedAt());
        return r;
    }
}
