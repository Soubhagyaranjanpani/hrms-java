// File: com/hrms/employee/application/EmployeeGradeService.java
package com.hrms.employee.application;

import com.hrms.employee.domain.EmployeeGrade;
import com.hrms.employee.infrastructure.EmployeeGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeGradeService {

    private final EmployeeGradeRepository gradeRepo;

    public List<EmployeeGrade> getAllActive() {
        return gradeRepo.findByIsActiveTrueOrderByLevelAsc();
    }

    public List<EmployeeGrade> getAll() {
        return gradeRepo.findAll();
    }

    public EmployeeGrade getById(Long id) {
        return gradeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found with id: " + id));
    }

    @Transactional
    public EmployeeGrade create(EmployeeGrade grade) {
        if (gradeRepo.existsByCode(grade.getCode())) {
            throw new RuntimeException("Grade with code '" + grade.getCode() + "' already exists");
        }
        return gradeRepo.save(grade);
    }

    @Transactional
    public EmployeeGrade update(Long id, EmployeeGrade updated) {
        EmployeeGrade existing = getById(id);
        existing.setCode(updated.getCode());
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setMinSalary(updated.getMinSalary());
        existing.setMaxSalary(updated.getMaxSalary());
        existing.setGradePay(updated.getGradePay());
        existing.setLevel(updated.getLevel());
        existing.setIsActive(updated.getIsActive());
        return gradeRepo.save(existing);
    }

    @Transactional
    public EmployeeGrade toggleStatus(Long id) {
        EmployeeGrade grade = getById(id);
        grade.setIsActive(!grade.getIsActive());
        return gradeRepo.save(grade);
    }
}