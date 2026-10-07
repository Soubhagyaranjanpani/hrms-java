package com.hrms.Recuirment.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="Rejection_reason")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejectionReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="reason_code", unique = true, nullable = false)
    private String reasonCode;
    @Column(name="reason_name",nullable = false,length = 100)
    private String reasonName;
    @Column(name="description")
    private String description;
    @Column(name="status")
    private String status;

    @CreationTimestamp
    @Column(name="created_at",updatable = false)
    private LocalDateTime createdAt;
    @Column(name="created_by")
    private String createdBy;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "reasonCategory_id", nullable = false)
    private ReasonCategory reasonCategory;
}
