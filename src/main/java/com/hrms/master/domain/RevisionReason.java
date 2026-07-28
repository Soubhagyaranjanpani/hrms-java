package com.hrms.master.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "revision_reason")
@Data
public class RevisionReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
}