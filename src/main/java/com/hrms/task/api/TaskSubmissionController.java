package com.hrms.task.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.task.application.GetTaskSubmissionsUseCase;
import com.hrms.task.application.ReviewSubmissionUseCase;
import com.hrms.task.application.SubmitTaskProofUseCase;
import com.hrms.task.dto.ReviewSubmissionRequest;
import com.hrms.task.dto.SubmissionResponse;
import com.hrms.task.dto.SubmitProofRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskSubmissionController {

    private final SubmitTaskProofUseCase submitTaskProofUseCase;
    private final ReviewSubmissionUseCase reviewSubmissionUseCase;
    private final GetTaskSubmissionsUseCase getTaskSubmissionsUseCase;

    // Employee: submit daily progress or final work with proof (multipart/form-data)
    @PostMapping(value = "/{id}/submissions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SubmissionResponse> submit(
            @PathVariable Long id,
            @ModelAttribute SubmitProofRequest req,
            Principal p) {
        return ResponseUtils.createSuccessResponse(
                submitTaskProofUseCase.execute(id, req, p.getName()),
                new TypeReference<>() {}
        );
    }

    // Anyone allowed to see the task: submission history
    @GetMapping("/{id}/submissions")
    public ApiResponse<List<SubmissionResponse>> list(@PathVariable Long id, Principal p) {
        return ResponseUtils.createSuccessResponse(
                getTaskSubmissionsUseCase.execute(id, p.getName()),
                new TypeReference<>() {}
        );
    }

    // Manager / assigner / Admin / HR: approve or reject
    @PostMapping("/submissions/{submissionId}/review")
    public ApiResponse<SubmissionResponse> review(
            @PathVariable Long submissionId,
            @RequestBody ReviewSubmissionRequest req,
            Principal p) {
        return ResponseUtils.createSuccessResponse(
                reviewSubmissionUseCase.execute(submissionId, req, p.getName()),
                new TypeReference<>() {}
        );
    }

    // Download proof file
    @GetMapping("/submissions/{submissionId}/proof")
    public ResponseEntity<Resource> proof(@PathVariable Long submissionId, Principal p) {
        GetTaskSubmissionsUseCase.ProofFile f = getTaskSubmissionsUseCase.proof(submissionId, p.getName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(f.filename()).build().toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(f.resource());
    }
}
