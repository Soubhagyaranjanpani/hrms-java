package com.hrms.employee.api;

import com.hrms.employee.application.*;
import com.hrms.employee.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/employee-profile")
@RequiredArgsConstructor
public class EmployeeProfileController {

    private final GetEmployeeFullProfileUseCase getEmployeeFullProfileUseCase;
    private final AddQualificationUseCase addQualificationUseCase;
    private final AddSkillUseCase addSkillUseCase;
    private final AddTrainingUseCase addTrainingUseCase;


    @PostMapping("/qualification")
    public String addQualification(@RequestBody QualificationRequest req, Principal p) {
        return addQualificationUseCase.execute(req, p.getName());
    }

    @PostMapping("/skill")
    public String addSkill(@RequestBody SkillRequest req, Principal p) {
        return addSkillUseCase.execute(req, p.getName());
    }

    @PostMapping("/training")
    public String addTraining(@RequestBody TrainingRequest req, Principal p) {
        return addTrainingUseCase.execute(req, p.getName());
    }
    @GetMapping("/full/{employeeId}")
    public Object getFullProfile(@PathVariable Long employeeId) {
        return getEmployeeFullProfileUseCase.execute(employeeId);
    }
}
