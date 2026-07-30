package com.hrms.disciplinary.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.disciplinary.application.*;
import com.hrms.disciplinary.dto.*;
import com.hrms.disciplinary.infrastructure.DisciplinaryRepository;
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
@RequestMapping("/api/disciplinary")
@RequiredArgsConstructor
public class DisciplinaryController {

    private final GetDisciplinaryListUseCase getListUseCase;
    private final GetEmployeeDisciplinaryHistoryUseCase historyUseCase;
    private final CreateDisciplinaryUseCase createUseCase;
    private final UpdateDisciplinaryRecordUseCase updateUseCase;
    private final UploadDisciplinaryDocumentUseCase uploadDocumentUseCase;
    private final DownloadDisciplinaryDocumentUseCase downloadDocumentUseCase;
    private final DisciplinaryRepository disciplinaryRepo;
    private final DisciplinaryMapper mapper;
    private final SetDisciplinaryStatusUseCase setStatusUseCase;

    @GetMapping
    public ApiResponse<DisciplinaryPageResponse> getList(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.execute(search, page, size), new TypeReference<>() {});
    }

    @GetMapping("/by-flag")
    public ApiResponse<DisciplinaryPageResponse> getDisciplinaryByFlag(
            @RequestParam Integer flag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.executeByFlag(flag, search, page, size), new TypeReference<>() {});
    }

    @GetMapping("/all")
    public ApiResponse<List<DisciplinaryRecordResponse>> getAllDisciplinary(
            @RequestParam Integer flag) {
        return ResponseUtils.createSuccessResponse(
                getListUseCase.executeAllByFlag(flag), new TypeReference<>() {});
    }

    @GetMapping("/data")
    public ApiResponse<?> getDisciplinaryData(
            @RequestParam int flag,
            @RequestParam(required = false) Long id) {

        if (flag == 0) {
            List<DisciplinaryRecordResponse> all = disciplinaryRepo.findAll()
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
            return ResponseUtils.createSuccessResponse(all, new TypeReference<>() {});
        } else if (flag == 1) {
            if (id == null) {
                throw new IllegalArgumentException("ID is required when flag=1");
            }
            DisciplinaryRecordResponse response = disciplinaryRepo.findById(id)
                    .map(mapper::toResponse)
                    .orElseThrow(() -> new RuntimeException("Record not found"));
            return ResponseUtils.createSuccessResponse(response, new TypeReference<>() {});
        } else {
            throw new IllegalArgumentException("Flag must be 0 or 1");
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<DisciplinaryRecordResponse> getOne(@PathVariable Long id) {
        DisciplinaryRecordResponse r = disciplinaryRepo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        return ResponseUtils.createSuccessResponse(r, new TypeReference<>() {});
    }

    @GetMapping("/employee/{empId}")
    public ApiResponse<List<DisciplinaryRecordResponse>> getEmployeeHistory(@PathVariable Long empId) {
        return ResponseUtils.createSuccessResponse(historyUseCase.execute(empId), new TypeReference<>() {});
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<DisciplinaryRecordResponse> create(@RequestBody CreateDisciplinaryRequest req) {
        return ResponseUtils.createSuccessResponse(createUseCase.execute(req), new TypeReference<>() {});
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<DisciplinaryRecordResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateDisciplinaryRequest req) {
        return ResponseUtils.createSuccessResponse(updateUseCase.execute(id, req), new TypeReference<>() {});
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<String> setStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        setStatusUseCase.execute(id, active);
        return ResponseUtils.createSuccessResponse(
                "Status updated to " + (active ? "ACTIVE" : "INACTIVE"),
                new TypeReference<>() {});
    }

    @PostMapping("/{id}/document")
    @PreAuthorize("hasRole('HR') or hasRole('Admin')")
    public ApiResponse<String> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws Exception {
        uploadDocumentUseCase.execute(id, file);
        return ResponseUtils.createSuccessResponse("Document uploaded successfully", new TypeReference<>() {});
    }

    @GetMapping("/{id}/document")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) throws Exception {
        DownloadDisciplinaryDocumentUseCase.DocumentFile file = downloadDocumentUseCase.execute(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getBytes());
    }
}