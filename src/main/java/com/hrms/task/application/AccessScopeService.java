package com.hrms.task.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.task.domain.RoleType;
import com.hrms.task.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Single place for ALL role / team rules.
 * ADMIN, HR  -> assign to MANAGERS, see everyone
 * MANAGER    -> assign to / see only their own direct reports (employee.manager = manager)
 * EMPLOYEE   -> see only themselves
 *
 * Matches your Employee entity: role.getName(), manager, department.
 */
@Service
@RequiredArgsConstructor
public class AccessScopeService {

    private final EmployeeRepository employeeRepo;

    public Employee currentUser(String email) {
        return employeeRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found: " + email));
    }

    public RoleType roleOf(Employee e) {
        return RoleType.from(e.getRole() != null ? e.getRole().getName() : null);   // Role is an entity: Admin / HR / MANAGER / ...
    }

    public boolean isPrivileged(Employee e) {
        return roleOf(e).isPrivileged();
    }

    public boolean isSame(Employee a, Employee b) {
        return a != null && b != null && a.getId().equals(b.getId());
    }

    public boolean isManagerOf(Employee manager, Employee emp) {
        return emp != null && emp.getManager() != null && isSame(emp.getManager(), manager);
    }

    public List<Employee> directReports(Employee manager) {
        return employeeRepo.findByManager_Id(manager.getId()).stream()
                .filter(e -> !Boolean.TRUE.equals(e.getIsDeleted()) && !Boolean.FALSE.equals(e.getIsActive()))
                .toList();
    }

    /** Employees whose progress / data the viewer may see. */
    public List<Employee> visibleEmployees(Employee viewer) {
        return switch (roleOf(viewer)) {
            case ADMIN, HR -> employeeRepo.findByIsActiveTrueAndIsDeletedFalse().stream()
                    .filter(e -> roleOf(e) == RoleType.MANAGER || roleOf(e) == RoleType.EMPLOYEE)
                    .toList();
            case MANAGER -> directReports(viewer);
            case EMPLOYEE -> List.of(viewer);
        };
    }

    public void assertCanAssign(Employee assigner, Employee assignee) {
        switch (roleOf(assigner)) {
            case ADMIN, HR -> {
                if (roleOf(assignee) != RoleType.MANAGER)
                    throw forbidden("Admin/HR can assign tasks to managers only");
            }
            case MANAGER -> {
                if (roleOf(assignee) != RoleType.EMPLOYEE || !isManagerOf(assigner, assignee))
                    throw forbidden("Managers can assign tasks only to their own employees");
            }
            case EMPLOYEE -> throw forbidden("Employees cannot assign tasks");
        }
    }

    public void assertCanViewEmployee(Employee viewer, Employee target) {
        if (isPrivileged(viewer) || isSame(viewer, target) || isManagerOf(viewer, target)) return;
        throw forbidden("You are not allowed to view this employee");
    }

    public boolean canViewTask(Employee viewer, Task t) {
        return isPrivileged(viewer)
                || isSame(viewer, t.getAssignedTo())
                || isSame(viewer, t.getAssignedBy())
                || isManagerOf(viewer, t.getAssignedTo());
    }

    public void assertCanViewTask(Employee viewer, Task t) {
        if (!canViewTask(viewer, t)) throw forbidden("You are not allowed to view this task");
    }

    /** Who may approve/reject work and force status changes. */
    public boolean canReviewTask(Employee viewer, Task t) {
        return isPrivileged(viewer)
                || isSame(viewer, t.getAssignedBy())
                || isManagerOf(viewer, t.getAssignedTo());
    }

    public ResponseStatusException forbidden(String msg) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, msg);
    }

    public ResponseStatusException badRequest(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
