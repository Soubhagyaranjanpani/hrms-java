package com.hrms.employee.api;

import com.hrms.employee.application.EmployeeConfirmationService;
import com.hrms.employee.dto.EmployeeConfirmationRequest;
import com.hrms.employee.dto.EmployeeConfirmationResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-confirmation")
public class EmployeeConfirmationController {

    private final EmployeeConfirmationService employeeConfirmationService;

    public EmployeeConfirmationController(EmployeeConfirmationService employeeConfirmationService) {
        this.employeeConfirmationService = employeeConfirmationService;
    }

    // ========================= CREATE =========================
    @PostMapping
    public EmployeeConfirmationResponse save(
            @RequestBody EmployeeConfirmationRequest request) {

        return employeeConfirmationService.save(request);
    }

    // ========================= GET ALL =========================
    @GetMapping
    public List<EmployeeConfirmationResponse> getAll(
            @RequestParam(defaultValue = "0") int flag) {

        return employeeConfirmationService.getAllByFlag(flag);
    }

    // ========================= GET BY ID =========================
    @GetMapping("/{id}")
    public EmployeeConfirmationResponse getById(@PathVariable Long id) {
        return employeeConfirmationService.getById(id);
    }

    // ========================= UPDATE =========================
    @PutMapping("/{id}")
    public EmployeeConfirmationResponse update(
            @PathVariable Long id,
            @RequestBody EmployeeConfirmationRequest request) {

        return employeeConfirmationService.update(id, request);
    }

    // ========================= DELETE (SOFT DELETE) =========================
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        employeeConfirmationService.delete(id);
        return "Employee Confirmation deleted successfully.";
    }
}