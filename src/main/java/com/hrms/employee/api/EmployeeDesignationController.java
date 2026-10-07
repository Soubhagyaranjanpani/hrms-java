package com.hrms.employee.api;

import com.hrms.employee.application.EmployeeDesignationService;
import com.hrms.employee.dto.EmployeeDesignationRequest;
import com.hrms.employee.dto.EmployeeDesignationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee-designation")
@RequiredArgsConstructor
public class EmployeeDesignationController {

    private final EmployeeDesignationService employeeDesignationService;

    @PostMapping
    public EmployeeDesignationResponse create(@RequestBody EmployeeDesignationRequest request) {
        return employeeDesignationService.create(request);
    }

    @GetMapping("/{id}")
    public EmployeeDesignationResponse getById(@PathVariable Long id) {
        return employeeDesignationService.getById(id);
    }

    @PutMapping("/{id}")
    public EmployeeDesignationResponse update(@PathVariable Long id,
                                              @RequestBody EmployeeDesignationRequest request) {
        return employeeDesignationService.update(id, request);
    }

    @GetMapping
    public List<EmployeeDesignationResponse> getAll(
            @RequestParam(defaultValue = "0") Integer flag) {
        return employeeDesignationService.getAll(flag);
    }

    // ✅ Status change API (Active / Inactive) — explicit true/false
    @PutMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam Boolean active) {
        employeeDesignationService.changeStatus(id, active);
        return "Employee Designation status updated successfully";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        employeeDesignationService.delete(id);
        return "Employee Designation Deleted Successfully";
    }
}