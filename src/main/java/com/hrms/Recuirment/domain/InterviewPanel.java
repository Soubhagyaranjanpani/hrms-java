package com.hrms.Recuirment.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hrms.employee.domain.Employee;
import com.hrms.master.domain.Department;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interview_panel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InterviewPanel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "panel_code", nullable = false, unique = true)
    private String panelCode;

    @Column(name = "panel_name", nullable = false)
    private String panelName;

    @Column(name="status")
    private String status;
    @CreationTimestamp
    @Column(name="created_at",updatable = false)
    private LocalDateTime createdAt;
    @Column(name="created_by")
    private String createdBy;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "primary_interviewer_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Employee primaryInterviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secondary_interviewer_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Employee secondaryInterviewer;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "interview_panel_member",
            joinColumns = @JoinColumn(name = "panel_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler",
            "manager",
            "subordinates"
    })
    private List<Employee> panelMembers = new ArrayList<>();
}
