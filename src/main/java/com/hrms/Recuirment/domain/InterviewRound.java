package com.hrms.Recuirment.domain;

import com.hrms.master.domain.Department;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="interview_round")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="round_code",nullable = false,unique = true)
    private String roundCode;
    @Column(name="round_name",nullable = false,length = 100)
    private String roundName;
    @Column(name="round_sequence",nullable = false,unique = true)
    private String roundSequence;
    // Applicable Department
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name="mandatory")
    private String mandatory;
//    @Min(0)
//    @Max(1000)
    @Column(name="maximum_score")
    private String maximumScore;
    @Column(name="passing_score")
    private String passingScore;

    @Column(name="status")
    private String status;
    @CreationTimestamp
    @Column(name="created_at",updatable = false)
    private LocalDateTime createdAt;
    @Column(name="created_by")
    private String createdBy;

}
