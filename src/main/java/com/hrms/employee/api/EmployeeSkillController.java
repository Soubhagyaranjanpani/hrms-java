package com.hrms.employee.api;

import com.hrms.employee.application.EmployeeSkillService;
import com.hrms.employee.dto.SkillDto;
import com.hrms.employee.dto.SkillRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee-skill")
@RequiredArgsConstructor
public class EmployeeSkillController {

    private final EmployeeSkillService employeeSkillService;

    @PostMapping
    public SkillDto create(@RequestBody SkillRequest request) {
        return employeeSkillService.create(request);
    }

    @GetMapping("/{id}")
    public SkillDto getById(@PathVariable Long id) {
        return employeeSkillService.getById(id);
    }

    @PutMapping("/{id}")
    public SkillDto update(@PathVariable Long id,
                           @RequestBody SkillRequest request) {
        return employeeSkillService.update(id, request);
    }

    @GetMapping
    public List<SkillDto> getAll(
            @RequestParam(defaultValue = "0") Integer flag) {
        return employeeSkillService.getAll(flag);
    }

    // ✅ Status change API (Active / Inactive) — explicit true/false
    @PutMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam Boolean active) {
        employeeSkillService.changeStatus(id, active);
        return "Employee Skill status updated successfully";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        employeeSkillService.delete(id);
        return "Employee Skill Deleted Successfully";
    }
}