package com.hrms.task.application;

import com.hrms.task.domain.Task;
import com.hrms.task.domain.TaskEscalation;
import com.hrms.task.infrastructure.TaskEscalationRepository;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskEscalationJob {

    private final TaskRepository taskRepo;
    private final TaskEscalationRepository escalationRepo;

    @Scheduled(cron = "0 0 * * * *")
    public void run() {

        List<Task> delayedTasks = taskRepo.findAll()
                .stream()
                .filter(t -> t.getDueDate() != null &&
                        t.getDueDate().isBefore(LocalDateTime.now()))
                .toList();

        for (Task t : delayedTasks) {

            TaskEscalation escalation = new TaskEscalation();
            escalation.setTask(t);
            escalation.setLevel(1);
            escalation.setTriggeredAt(LocalDateTime.now());
            escalation.setStatus("PENDING");

            escalationRepo.save(escalation);
        }
    }
}
