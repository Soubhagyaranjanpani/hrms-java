package com.hrms.task.domain;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "task_change_requests")
public class TaskChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(length = 1000)
    private String reason;

    private LocalDateTime requestedDeadline;

    private String status; // PENDING, APPROVED, REJECTED

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
