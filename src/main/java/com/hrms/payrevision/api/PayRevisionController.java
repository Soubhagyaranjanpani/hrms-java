package com.hrms.payrevision.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.payrevision.application.*;
import com.hrms.payrevision.dto.*;
import com.hrms.payrevision.infrastructure.PayRevisionRepository;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pay-revisions")
@RequiredArgsConstructor
public class PayRevisionController {

    private final GetPayRevisionListUseCase getListUseCase;
    private final GetEmployeePayRevisionHistoryUseCase historyUseCase;
    private final GetPayRevisionReasonsUseCase getReasonsUseCase;
    private final CreatePayRevisionUseCase createUseCase;
    private final UpdatePayRevisionRecordUseCase updateUseCase;
    private final UploadPayRevisionDocumentUseCase uploadDocumentUseCase;
    private final DownloadPayRevisionDocumentUseCase downloadDocumentUseCase;
    private final PayRevisionRepository payRevisionRepo;
    private final PayRevisionMapper mapper;
    private final SetPayRevisionStatusUseCase setStatusUseCase;

    // ── Master dropdown: Reason (Annual Increment / Promotion / Performance Based / Market Correction / ...) ──
    @GetMapping("/reasons")
    public ApiResponse<List<PayRevisionReasonResponse>> getReasons() {
        return ResponseUtils.createSuccessResponse(getReasonsUseCase.execute(), new TypeReference<>() {
        });
    }

    @GetMapping
    public ApiResponse<PayRevisionPageResponse> getList(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.execute(search, page, size), new TypeReference<>() {
                });
    }

    @GetMapping("/by-flag")
    public ApiResponse<PayRevisionPageResponse> getPayRevisionsByFlag(
            @RequestParam Integer flag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.executeByFlag(flag, search, page, size), new TypeReference<>() {
                });
    }

    @GetMapping("/all")
    public ApiResponse<List<PayRevisionRecordResponse>> getAllPayRevisions(
            @RequestParam Integer flag) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.executeAllByFlag(flag), new TypeReference<>() {
                });
    }

    @GetMapping("/data")
    public ApiResponse<?> getPayRevisionData(
            @RequestParam int flag,
            @RequestParam(required = false) Long id) {

        if (flag == 0) {
            List<PayRevisionRecordResponse> all = payRevisionRepo.findAll()
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
            return ResponseUtils.createSuccessResponse(all, new TypeReference<>() {
            });
        } else if (flag == 1) {
            if (id == null) {
                throw new IllegalArgumentException("ID is required when flag=1");
            }
            PayRevisionRecordResponse response = payRevisionRepo.findById(id)
                    .map(mapper::toResponse)
                    .orElseThrow(() -> new RuntimeException("Record not found"));
            return ResponseUtils.createSuccessResponse(response, new TypeReference<>() {
            });
        } else {
            throw new IllegalArgumentException("Flag must be 0 or 1");
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<PayRevisionRecordResponse> getOne(@PathVariable Long id) {
        PayRevisionRecordResponse r = payRevisionRepo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        return ResponseUtils.createSuccessResponse(r, new TypeReference<>() {
        });
    }

    @GetMapping("/employee/{empId}")
    public ApiResponse<List<PayRevisionRecordResponse>> getEmployeeHistory(@PathVariable Long empId) {
        return ResponseUtils.createSuccessResponse(historyUseCase.execute(empId), new TypeReference<>() {
        });
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<PayRevisionRecordResponse> create(@RequestBody CreatePayRevisionRequest req) {
        return ResponseUtils.createSuccessResponse(createUseCase.execute(req), new TypeReference<>() {
        });
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<PayRevisionRecordResponse> update(
            @PathVariable Long id,
            @RequestBody UpdatePayRevisionRequest req) {
        return ResponseUtils.createSuccessResponse(updateUseCase.execute(id, req), new TypeReference<>() {
        });
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<String> setStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        setStatusUseCase.execute(id, active);
        return ResponseUtils.createSuccessResponse(
                "Status updated to " + (active ? "ACTIVE" : "INACTIVE"),
                new TypeReference<>() {
                });
    }

    @PostMapping("/{id}/document")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<String> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws Exception {
        uploadDocumentUseCase.execute(id, file);
        return ResponseUtils.createSuccessResponse("Document uploaded successfully", new TypeReference<>() {
        });
    }

    @GetMapping("/{id}/document")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) throws Exception {
        DownloadPayRevisionDocumentUseCase.DocumentFile file = downloadDocumentUseCase.execute(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getBytes());
    }
}
