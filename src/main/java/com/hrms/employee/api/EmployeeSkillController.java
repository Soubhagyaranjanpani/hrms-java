package com.hrms.employee.api;

import com.hrms.employee.application.AddSkillUseCase;
import com.hrms.employee.dto.SkillRequest;
import com.hrms.employee.dto.SkillDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@RestController
@RequestMapping("/api/employee-skill")
@RequiredArgsConstructor
public class EmployeeSkillController {

    private final AddSkillUseCase addSkillUseCase;


    @PostMapping
    public String addSkill(@RequestBody SkillRequest request, Principal principal) {
        return addSkillUseCase.execute(request, principal.getName());
    }

    @GetMapping
    public List<SkillDto> getAll(@RequestParam(defaultValue = "0") Integer flag) {
        return addSkillUseCase.execute(flag);
    }

    @GetMapping("/employee/{id}")
    public List<SkillDto> getByEmployee(@PathVariable Long id) {
        return addSkillUseCase.execute(id);
    }

    @PutMapping("/{id}")
    public SkillDto update(@PathVariable Long id,
                           @RequestBody SkillRequest request,
                           @RequestParam String user) {
        return addSkillUseCase.execute(id, request, user);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id,
                         @RequestParam String user) {
        return addSkillUseCase.executeDelete(id, user);
    }
}