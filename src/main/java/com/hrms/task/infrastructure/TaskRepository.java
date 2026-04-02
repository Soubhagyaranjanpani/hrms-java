package com.hrms.task.infrastructure;

import com.hrms.employee.domain.Employee;
import com.hrms.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedToAndIsDeletedFalse(Employee employee);

    List<Task> findByDepartment_IdAndIsDeletedFalse(Long departmentId);
}
