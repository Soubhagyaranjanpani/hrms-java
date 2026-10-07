package com.hrms.master.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "source_category")
public class SourceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at")       // ✅ DateTime ke liye "at"
    private LocalDateTime createdAt;

    @Column(name = "created_by")       // ✅ String ke liye "by"
    private String createdBy;

    @Column(name = "status")
    private String status;



    // ⭐ Ye method automatically call hoga INSERT se pehle
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
        if (this.createdBy == null) {
            this.createdBy = "SYSTEM";    // ya logged-in user
        }
    }

    @PreUpdate
    public void preUpdate() {
        // Agar update timestamp chahiye toh yahan
    }
}