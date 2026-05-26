package com.hrms.task.application;



import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.task.domain.TaskStatus;
import com.hrms.task.dto.EmployeeTaskStatsResponse;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GetEmployeeTaskStatsUseCase {

    private final TaskRepository taskRepo;
    private final EmployeeRepository employeeRepo;

    public EmployeeTaskStatsResponse execute(Long employeeId) {

        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeTaskStatsResponse res = new EmployeeTaskStatsResponse();

        res.setEmployeeId(emp.getId());
        res.setEmployeeName(emp.getFirstName() + " " + emp.getLastName());

        // 🔥 Total Assigned
        Long total = taskRepo.countByAssignedToIdAndIsDeletedFalse(employeeId);
        res.setTotalAssigned(total != null ? total : 0);

        // 🔥 Status wise
        res.setPendingApproval(
                safeCount(taskRepo.countByAssignedToIdAndStatusAndIsDeletedFalse(
                        employeeId, TaskStatus.PENDING_APPROVAL))
        );

        res.setInProgress(
                safeCount(taskRepo.countByAssignedToIdAndStatusAndIsDeletedFalse(
                        employeeId, TaskStatus.IN_PROGRESS))
        );

        res.setCompleted(
                safeCount(taskRepo.countByAssignedToIdAndStatusAndIsDeletedFalse(
                        employeeId, TaskStatus.COMPLETED))
        );

        // 🔥 Overdue (dueDate passed but not completed)
        Long overdue = taskRepo.countByAssignedToIdAndDueDateBeforeAndStatusNotAndIsDeletedFalse(
                employeeId,
                LocalDateTime.now(),
                TaskStatus.COMPLETED
        );

        res.setOverdue(overdue != null ? overdue : 0);

        return res;
    }

    private Long safeCount(Long value) {
        return value != null ? value : 0;
    }
}
