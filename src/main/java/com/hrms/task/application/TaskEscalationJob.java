package com.hrms.task.application;

import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskEscalation;
import com.hrms.task.infrastructure.TaskEscalationRepository;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskEscalationJob {

    private final TaskRepository taskRepo;
    private final TaskEscalationRepository escalationRepo;

    // runs every hour
    @Scheduled(cron = "0 0 * * * *")
    public void run() {

        List<Task> overdueTasks = taskRepo.findOverdueTasks();

        log.info("Escalation job running — {} overdue tasks found", overdueTasks.size());

        for (Task t : overdueTasks) {

            // skip if already has a pending escalation
            boolean alreadyEscalated = escalationRepo.findByTask(t)
                    .stream()
                    .anyMatch(e -> "PENDING".equals(e.getStatus()));

            if (alreadyEscalated) continue;

            TaskEscalation escalation = new TaskEscalation();
            escalation.setTask(t);
            escalation.setLevel(1);
            escalation.setEscalatedTo(t.getAssignedBy()); // escalate to manager (creator)
            escalation.setTriggeredAt(LocalDateTime.now());
            escalation.setStatus("PENDING");

            escalationRepo.save(escalation);

            log.info("Escalation created for task id={} title={}", t.getId(), t.getTitle());
        }
    }
}