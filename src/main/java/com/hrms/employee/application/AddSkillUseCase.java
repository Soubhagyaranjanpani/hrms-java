package com.hrms.employee.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeSkill;
import com.hrms.employee.dto.SkillRequest;
import com.hrms.employee.dto.SkillDto;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.employee.infrastructure.EmployeeSkillRepository;
import com.hrms.audit.application.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AddSkillUseCase {

    private final EmployeeRepository employeeRepository;
    private final EmployeeSkillRepository skillRepository;
    private final AuditLogService auditLogService;

    // ---------- CREATE ----------
    public SkillDto create(SkillRequest request, String user) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeSkill skill = new EmployeeSkill();
        skill.setEmployee(employee);
        skill.setSkillName(request.getSkillName());
        skill.setProficiency(request.getProficiency());
        skill.setFlag(1);

        skill = skillRepository.save(skill);
        auditLogService.log("SKILL", skill.getId(), "CREATE", user, null, skill);
        return mapToDto(skill);
    }

    // ---------- GET ALL (fixed for flag=0 → all) ----------
    public List<SkillDto> getAll(Integer flag) {
        List<EmployeeSkill> skills;
        if (flag == null || flag == 0) {
            skills = skillRepository.findAll();          // all skills
        } else {
            skills = skillRepository.findByFlag(flag);   // filter by flag (e.g., 1)
        }
        return skills.stream().map(this::mapToDto).collect(Collectors.toList());
    }







    // ---------- GET BY ID ----------
    public SkillDto getById(Long id) {
        EmployeeSkill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
        return mapToDto(skill);
    }

    // ---------- GET BY EMPLOYEE ----------
    public List<SkillDto> getByEmployee(Long employeeId) {
        return skillRepository.findByEmployee_Id(employeeId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ---------- UPDATE ----------
    public SkillDto update(Long id, SkillRequest request, String user) {
        EmployeeSkill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skill.setSkillName(request.getSkillName());
        skill.setProficiency(request.getProficiency());

        skill = skillRepository.save(skill);
        auditLogService.log("SKILL", skill.getId(), "UPDATE", user, null, skill);
        return mapToDto(skill);
    }

    // ---------- DELETE (soft) ----------
    public void delete(Long id, String user) {
        EmployeeSkill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
        skill.setFlag(0);
        skillRepository.save(skill);
        auditLogService.log("SKILL", id, "DELETE", user, skill, null);
    }

    // ---------- BACKWARD-COMPATIBLE EXECUTE METHODS ----------
    public String execute(SkillRequest request, String user) {
        create(request, user);
        return "Skill added successfully";
    }

    public List<SkillDto> execute(Integer flag) {
        return getAll(flag);
    }

    public List<SkillDto> execute(Long employeeId) {
        return getByEmployee(employeeId);
    }

    public SkillDto execute(Long id, SkillRequest request, String user) {
        return update(id, request, user);
    }

    public String executeDelete(Long id, String user) {
        delete(id, user);
        return "Skill deleted successfully";
    }

    // ---------- MAPPING ----------
    private SkillDto mapToDto(EmployeeSkill skill) {
        SkillDto dto = new SkillDto();
        dto.setId(skill.getId());
        dto.setEmployeeId(skill.getEmployee().getId());
        dto.setSkillName(skill.getSkillName());
        dto.setProficiency(skill.getProficiency());
        return dto;
    }
}