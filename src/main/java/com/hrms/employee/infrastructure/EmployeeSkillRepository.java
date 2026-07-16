package com.hrms.employee.infrastructure;

import com.hrms.employee.domain.EmployeeSkill;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeSkillRepository extends JpaRepository<EmployeeSkill, Long> {

    @EntityGraph(attributePaths = {"employee", "skill"})
    List<EmployeeSkill> findByIsDeletedFalse();

    @EntityGraph(attributePaths = {"employee", "skill"})
    List<EmployeeSkill> findByIsActiveTrueAndIsDeletedFalse();

    @EntityGraph(attributePaths = {"employee", "skill"})
    List<EmployeeSkill> findByEmployee_IdAndIsDeletedFalse(Long employeeId);

    Optional<EmployeeSkill> findFirstByEmployee_IdAndSkill_IdAndIsActiveTrueAndIsDeletedFalse(
            Long employeeId, Long skillId);

    List<EmployeeSkill> findByEmployee_Id(Long employeeId);

    List<EmployeeSkill> findBySkill_Name(String skillName);
}