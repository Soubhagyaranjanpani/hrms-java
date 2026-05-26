// File: com/hrms/payroll/domain/SalaryConfiguration.java
package com.hrms.payroll.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "salary_configurations")
public class SalaryConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    @Column(name = "config_name", nullable = false, length = 255)
    private String configName;

    @Column(name = "config_value", nullable = false)
    private Double configValue;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String category;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}