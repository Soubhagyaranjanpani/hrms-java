package com.hrms.Recuirment.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="Candidate_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //bASIC FIELDS
    @Column(name="statusCode",nullable = false,unique = true)
    private String statusCode;
    @Column(name="statusName",nullable = false,length = 100)
    private String statusName;
    @Column(name="displayOrder",nullable = false,unique = true)
    private Integer displayOrder;
    @Column(name="finalStatus",nullable = false,length = 1)
    private String finalStatus;
    @Column(name="statusColor",length = 20)
    private String statusColor;
    @Column(name="description",nullable = false,length = 100)
    private String description;
    //statusCategory
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="statusCategory_id",nullable = false)
    private StatusCategory statusCategory;

    @Column(name="status")
    private String status;
    @CreationTimestamp
    @Column(name="created_at",updatable = false)
    private LocalDateTime createdAt;
    @Column(name="created_by")
    private String createdBy;
}
