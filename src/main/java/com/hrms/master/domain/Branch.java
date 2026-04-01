package com.hrms.master.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "branch",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"code"}),
                @UniqueConstraint(columnNames = {"name"})
        }
)
@Data
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique business identifier (important)
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String country;

    @Column(length = 10)
    private String pincode;

    // Status
    private Boolean isActive = true;

    // Soft delete
    private Boolean isDeleted = false;

    // Audit fields
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    // Lifecycle hooks
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isDeleted = false;
        this.isActive = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
