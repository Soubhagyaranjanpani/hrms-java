package com.hrms.Appointment_Type.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "appointment_type",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"appointment_type"})
        }
)
@Data
public class Appointment_Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "appointment_type",
            nullable = false,
            unique = true,
            length = 100
    )
    private String appointmentType;

    // Status
    private Boolean isActive = true;

    // Soft Delete
    private Boolean isDeleted = false;

    // Audit Fields
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.isDeleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}