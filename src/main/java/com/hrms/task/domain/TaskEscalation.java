package com.hrms.task.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "task_escalations")
public class TaskEscalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    private Integer level;

    @ManyToOne
    @JoinColumn(name = "escalated_to_id")
    private Employee escalatedTo;

    private LocalDateTime triggeredAt;

    private String status; // PENDING, RESOLVED
}