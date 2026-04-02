package com.hrms.task.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TaskEscalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Task task;

    private Integer level;

    @ManyToOne
    private Employee escalatedTo;

    private LocalDateTime triggeredAt;

    private String status; // PENDING, RESOLVED
}
