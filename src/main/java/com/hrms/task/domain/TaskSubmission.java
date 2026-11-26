package com.hrms.task.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** One work/progress submission (with proof) made by the employee the task is assigned to. */
@Entity
@Getter
@Setter
@Table(name = "task_submissions")
public class TaskSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "submitted_by_id")
    private Employee submittedBy;

    @Column(length = 2000)
    private String note;

    private String proofLink;
    private String proofFileName;   // original name shown to the user
    private String proofFilePath;   // relative path on disk

    private Integer progressPercent;
    private Double hoursSpent;

    private Boolean finalSubmission = false;  // true = "I'm done, please review"
    private Boolean late = false;             // submitted after task due date

    private LocalDate submissionDate;         // used for the daily graph
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status = SubmissionStatus.SUBMITTED;

    @ManyToOne
    @JoinColumn(name = "reviewed_by_id")
    private Employee reviewedBy;

    @Column(length = 1000)
    private String reviewComment;

    private LocalDateTime reviewedAt;
}
