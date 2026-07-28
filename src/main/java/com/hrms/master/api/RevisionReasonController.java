package com.hrms.master.api;

import com.hrms.master.application.RevisionReasonService;
import com.hrms.master.dto.RevisionReasonRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/revision-reasons")
@RequiredArgsConstructor
public class RevisionReasonController {

    private final RevisionReasonService service;

    @GetMapping("/list")
    public ResponseEntity<List<RevisionReasonRequest>> list() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<RevisionReasonRequest> create(@RequestBody RevisionReasonRequest dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/update")
    public ResponseEntity<RevisionReasonRequest> update(@RequestBody RevisionReasonRequest dto) {
        return ResponseEntity.ok(service.update(dto));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<RevisionReasonRequest> updateStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.updateStatus(id));
    }
}