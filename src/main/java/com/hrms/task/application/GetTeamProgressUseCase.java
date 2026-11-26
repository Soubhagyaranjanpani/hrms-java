package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.task.domain.TaskSubmission;
import com.hrms.task.dto.EmployeeProgressResponse;
import com.hrms.task.infrastructure.TaskSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Progress of every employee the viewer is allowed to see.
 *   MANAGER   -> own direct reports only
 *   ADMIN/HR  -> everyone (optionally one department)
 *   EMPLOYEE  -> only himself
 */
@Service
@RequiredArgsConstructor
public class GetTeamProgressUseCase {

    private final AccessScopeService scope;
    private final TaskSubmissionRepository submissionRepo;
    private final GetEmployeeProgressUseCase progressUseCase;

    public List<EmployeeProgressResponse> execute(String viewerEmail, Long departmentId,
                                                  LocalDate from, LocalDate to) {
        Employee viewer = scope.currentUser(viewerEmail);
        LocalDate[] range = GetEmployeeProgressUseCase.normalizeRange(from, to);

        List<Employee> people = scope.visibleEmployees(viewer).stream()
                .filter(e -> departmentId == null
                        || (e.getDepartment() != null && departmentId.equals(e.getDepartment().getId())))
                .toList();
        if (people.isEmpty()) return List.of();

        List<Long> ids = people.stream().map(Employee::getId).toList();
        Map<Long, List<TaskSubmission>> subsByEmp = submissionRepo
                .findBySubmittedByIdInAndSubmissionDateBetween(ids, range[0], range[1])
                .stream().collect(Collectors.groupingBy(s -> s.getSubmittedBy().getId()));

        return people.stream()
                .map(e -> progressUseCase.build(e, subsByEmp.getOrDefault(e.getId(), List.of()), range[0], range[1]))
                .toList();
    }
}
