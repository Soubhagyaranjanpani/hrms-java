package com.hrms.employee.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.application.EmployeeGradeService;
import com.hrms.employee.domain.EmployeeGrade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class EmployeeGradeController {

    private final EmployeeGradeService gradeService;

    // ── GET all active grades (for dropdowns) ──
    @GetMapping
    public ApiResponse<List<EmployeeGrade>> getAllActive() {
        return ResponseUtils.createSuccessResponse(
                gradeService.getAllActive(),
                new TypeReference<>() {}
        );
    }

    // ── GET all grades (including inactive - for admin) ──
    @GetMapping("/all")
    public ApiResponse<List<EmployeeGrade>> getAll() {
        return ResponseUtils.createSuccessResponse(
                gradeService.getAll(),
                new TypeReference<>() {}
        );
    }

    // ── GET by ID ──
    @GetMapping("/{id}")
    public ApiResponse<EmployeeGrade> getById(@PathVariable Long id) {
        return ResponseUtils.createSuccessResponse(
                gradeService.getById(id),
                new TypeReference<>() {}
        );
    }

    // ── CREATE ──
    @PostMapping
    public ApiResponse<EmployeeGrade> create(@RequestBody EmployeeGrade grade) {
        return ResponseUtils.createSuccessResponse(
                gradeService.create(grade),
                new TypeReference<>() {}
        );
    }

    // ── UPDATE ──
    @PutMapping("/{id}")
    public ApiResponse<EmployeeGrade> update(
            @PathVariable Long id,
            @RequestBody EmployeeGrade grade) {
        return ResponseUtils.createSuccessResponse(
                gradeService.update(id, grade),
                new TypeReference<>() {}
        );
    }

    // ── TOGGLE ACTIVE/INACTIVE (Soft Delete) ──
    @PutMapping("/{id}/toggle-status")
    public ApiResponse<EmployeeGrade> toggleStatus(@PathVariable Long id) {
        return ResponseUtils.createSuccessResponse(
                gradeService.toggleStatus(id),
                new TypeReference<>() {}
        );
    }
}