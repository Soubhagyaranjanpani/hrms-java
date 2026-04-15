package com.hrms.task.domain;

import com.hrms.employee.domain.Employee;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;   // LOW, MEDIUM, HIGH, CRITICAL

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING_APPROVAL;    // PENDING_APPROVAL, IN_PROGRESS, etc.

    private Integer progress = 0;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private Employee assignedTo;

    @ManyToOne
    @JoinColumn(name = "assigned_by_id")
    private Employee assignedBy;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    // ✅ Parent Task
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> subtasks;

    private LocalDateTime startDate;
    private LocalDateTime dueDate;

    private Integer estimatedHours;
    private Integer actualHours;

    private String effort;
    private String tags;

    private Boolean isActive = true;
    private Boolean isDeleted = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}