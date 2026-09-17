package com.hrms.Recuirment.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="workmode")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="workMode",updatable = false)
    private String workModeName;
    @Column(name="status")
    private String status;
    @CreationTimestamp
    @Column(name="created_at",updatable = false)
    private LocalDateTime createdAt;
    @Column(name="created_by")
    private String createdBy;
}
