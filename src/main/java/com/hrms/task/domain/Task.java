package com.hrms.task.domain;

import com.hrms.employee.domain.Employee;
import com.hrms.master.domain.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private String priority; // LOW, MEDIUM, HIGH

    private String status; // TODO, IN_PROGRESS, DONE

    @ManyToOne
    private Employee assignedTo;

    @ManyToOne
    private Employee assignedBy;

    @ManyToOne
    private Department department;

    private LocalDateTime startDate;
    private LocalDateTime dueDate;

    private Integer estimatedHours;
    private Integer actualHours;

    @ManyToOne
    private Task parentTask;

    private Boolean isActive = true;
    private Boolean isDeleted = false;

    private LocalDateTime createdAt;
}
