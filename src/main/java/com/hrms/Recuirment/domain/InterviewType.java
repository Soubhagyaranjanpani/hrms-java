package com.hrms.Recuirment.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="interview_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="interviewTypeCode",nullable = false,unique = true)
    private String interviewTypeCode;
    @Column(name="interviewTypeName",nullable = false,length = 100)
    private String interviewTypeName;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="interviewMode_id",nullable = false)
    private InterviewMode interviewMode;

    @Column(name="description")
    private String description;

    @Column(name="status")
    private String status;
    @CreationTimestamp
    @Column(name="created_at",updatable = false)
    private LocalDateTime createdAt;
    @Column(name="created_by")
    private String createdBy;
}
