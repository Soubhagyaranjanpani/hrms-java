package com.hrms.employee.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeSkill;
import com.hrms.employee.dto.SkillDto;
import com.hrms.employee.dto.SkillRequest;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.employee.infrastructure.EmployeeSkillRepository;
import com.hrms.master.domain.Skill;
import com.hrms.master.infrastructure.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * NOTE: This service throws {@link EmployeeSkillNotFoundException} /
 * {@link RelatedEntityNotFoundException} instead of plain RuntimeException so that
 * a @ControllerAdvice can map them to proper HTTP status codes (404 vs 400/409).
 *
 * Structured identically to EmployeeDesignationService.
 */
@Service
@RequiredArgsConstructor
public class EmployeeSkillService {

    private final EmployeeSkillRepository employeeSkillRepository;
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public SkillDto create(SkillRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RelatedEntityNotFoundException(
                        "Employee not found with id: " + request.getEmployeeId()));

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new RelatedEntityNotFoundException(
                        "Skill not found with id: " + request.getSkillId()));

        employeeSkillRepository
                .findFirstByEmployee_IdAndSkill_IdAndIsActiveTrueAndIsDeletedFalse(
                        request.getEmployeeId(), request.getSkillId())
                .ifPresent(existing -> {
                    throw new DuplicateActiveSkillException(
                            "Employee " + request.getEmployeeId()
                                    + " already has skill " + request.getSkillId() + " active");
                });

        EmployeeSkill entity = new EmployeeSkill();
        entity.setEmployee(employee);
        entity.setSkill(skill);
        entity.setCreatedDate(LocalDate.now());
        entity.setIsActive(true);
        entity.setIsDeleted(false);

        entity = employeeSkillRepository.save(entity);
        return mapToResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getAll(Integer flag) {

        boolean activeOnly = Objects.equals(flag, 1);

        List<EmployeeSkill> list = activeOnly
                ? employeeSkillRepository.findByIsActiveTrueAndIsDeletedFalse()
                : employeeSkillRepository.findByIsDeletedFalse();

        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SkillDto getById(Long id) {
        EmployeeSkill entity = findActiveOrThrow(id);
        return mapToResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getByEmployee(Long employeeId) {
        return employeeSkillRepository.findByEmployee_IdAndIsDeletedFalse(employeeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SkillDto update(Long id, SkillRequest request) {

        EmployeeSkill entity = findActiveOrThrow(id);

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RelatedEntityNotFoundException(
                        "Employee not found with id: " + request.getEmployeeId()));

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new RelatedEntityNotFoundException(
                        "Skill not found with id: " + request.getSkillId()));

        entity.setEmployee(employee);
        entity.setSkill(skill);
        entity.setUpdatedDate(LocalDate.now());

        entity = employeeSkillRepository.save(entity);
        return mapToResponse(entity);
    }

    /**
     * Sets the active/inactive status explicitly based on the given value.
     */
    @Transactional
    public void changeStatus(Long id, Boolean active) {
        EmployeeSkill entity = findActiveOrThrow(id);
        entity.setIsActive(active);
        entity.setUpdatedDate(LocalDate.now());
        employeeSkillRepository.save(entity);
    }

    @Transactional
    public void delete(Long id) {

        EmployeeSkill entity = employeeSkillRepository.findById(id)
                .orElseThrow(() -> new EmployeeSkillNotFoundException(
                        "Record not found with id: " + id));

        if (Boolean.TRUE.equals(entity.getIsDeleted())) {
            return;
        }

        entity.setIsDeleted(true);
        employeeSkillRepository.save(entity);
    }

    private EmployeeSkill findActiveOrThrow(Long id) {
        EmployeeSkill entity = employeeSkillRepository.findById(id)
                .orElseThrow(() -> new EmployeeSkillNotFoundException(
                        "Record not found with id: " + id));

        if (Boolean.TRUE.equals(entity.getIsDeleted())) {
            throw new EmployeeSkillNotFoundException(
                    "Record has been deleted with id: " + id);
        }
        return entity;
    }

    private SkillDto mapToResponse(EmployeeSkill entity) {

        String firstName = entity.getEmployee().getFirstName() != null
                ? entity.getEmployee().getFirstName() : "";
        String lastName = entity.getEmployee().getLastName() != null
                ? entity.getEmployee().getLastName() : "";

        SkillDto dto = new SkillDto();
        dto.setId(entity.getId());
        dto.setEmployeeId(entity.getEmployee().getId());
        dto.setEmployeeName((firstName + " " + lastName).trim());
        dto.setSkillId(entity.getSkill().getId());
        dto.setSkillName(entity.getSkill().getName());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setIsActive(entity.getIsActive());
        dto.setIsDeleted(entity.getIsDeleted());
        return dto;
    }

    public static class DuplicateActiveSkillException extends RuntimeException {
        public DuplicateActiveSkillException(String message) {
            super(message);
        }
    }

    public static class EmployeeSkillNotFoundException extends RuntimeException {
        public EmployeeSkillNotFoundException(String message) {
            super(message);
        }
    }

    public static class RelatedEntityNotFoundException extends RuntimeException {
        public RelatedEntityNotFoundException(String message) {
            super(message);
        }
    }
}