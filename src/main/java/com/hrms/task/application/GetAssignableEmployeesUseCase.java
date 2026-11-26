package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.task.domain.RoleType;
import com.hrms.task.dto.AssigneeOptionResponse;
import com.hrms.task.dto.DepartmentAssigneesResponse;
import com.hrms.employee.infrastructure.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Feeds the "Assign To" dropdown, grouped department-wise.
 *   ADMIN / HR -> all managers
 *   MANAGER    -> only his own direct reports
 *   EMPLOYEE   -> 403
 */
@Service
@RequiredArgsConstructor
public class GetAssignableEmployeesUseCase {

    private final AccessScopeService scope;
    private final EmployeeRepository employeeRepo;

    public List<DepartmentAssigneesResponse> execute(String email, Long departmentId) {

        Employee me = scope.currentUser(email);

        List<Employee> candidates = switch (scope.roleOf(me)) {
            case ADMIN, HR -> employeeRepo.findByIsActiveTrueAndIsDeletedFalse().stream()
                    .filter(e -> scope.roleOf(e) == RoleType.MANAGER).toList();
            case MANAGER -> scope.directReports(me).stream()
                    .filter(e -> scope.roleOf(e) == RoleType.EMPLOYEE).toList();
            case EMPLOYEE -> throw scope.forbidden("Employees cannot assign tasks");
        };

        Map<Long, List<Employee>> byDept = candidates.stream()
                .filter(e -> !scope.isSame(e, me))
                .filter(e -> departmentId == null
                        || (e.getDepartment() != null && departmentId.equals(e.getDepartment().getId())))
                .collect(Collectors.groupingBy(
                        e -> e.getDepartment() != null ? e.getDepartment().getId() : -1L,
                        LinkedHashMap::new, Collectors.toList()));

        return byDept.entrySet().stream().map(en -> {
            List<Employee> list = en.getValue();
            Employee first = list.get(0);

            DepartmentAssigneesResponse d = new DepartmentAssigneesResponse();
            d.setDepartmentId(en.getKey() == -1L ? null : en.getKey());
            d.setDepartmentName(first.getDepartment() != null ? first.getDepartment().getName() : "No Department");
            d.setEmployees(list.stream().map(this::option)
                    .sorted(Comparator.comparing(AssigneeOptionResponse::getName, String.CASE_INSENSITIVE_ORDER))
                    .toList());
            return d;
        }).sorted(Comparator.comparing(DepartmentAssigneesResponse::getDepartmentName,
                String.CASE_INSENSITIVE_ORDER)).toList();
    }

    private AssigneeOptionResponse option(Employee e) {
        AssigneeOptionResponse o = new AssigneeOptionResponse();
        o.setId(e.getId());
        o.setName(e.getFirstName() + " " + e.getLastName());
        o.setEmail(e.getEmail());
        o.setRole(scope.roleOf(e).name());
        if (e.getDepartment() != null) {
            o.setDepartmentId(e.getDepartment().getId());
            o.setDepartment(e.getDepartment().getName());
        }
        return o;
    }
}
