package com.hrms.employee.api;

import com.hrms.employee.application.EmployeeTypeService;
import com.hrms.employee.domain.EmployeeType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employment-type")
public class EmployeeTypeController {

    private final EmployeeTypeService service;

    public EmployeeTypeController(EmployeeTypeService service) {
        this.service = service;
    }

    @PostMapping
    public EmployeeType create(@RequestBody EmployeeType employmentType) {
        return service.create(employmentType);
    }

    @GetMapping
    public List<EmployeeType> getAll(
            @RequestParam(defaultValue = "0") int flag) {
        return service.getAllByFlag(flag);
    }

    @GetMapping("/{id}")
    public EmployeeType getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public EmployeeType update(
            @PathVariable Long id,
            @RequestBody EmployeeType employmentType) {

        return service.update(id, employmentType);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Deleted Successfully";
    }
}