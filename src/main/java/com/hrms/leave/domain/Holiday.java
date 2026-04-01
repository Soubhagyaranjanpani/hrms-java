package com.hrms.leave.domain;

import com.hrms.leave.domain.enums.HolidayType;
import com.hrms.master.domain.Branch;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "holiday",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"date", "branch_id"})
        }
)
@Data
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Holiday date
    @Column(nullable = false)
    private LocalDate date;

    // Holiday name (e.g., Diwali, Republic Day)
    @Column(nullable = false, length = 100)
    private String name;

    // Optional description
    @Column(length = 255)
    private String description;

    // Branch-specific holiday (NULL = company-wide holiday)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    // Optional: type (GOVERNMENT, COMPANY, OPTIONAL)
    @Enumerated(EnumType.STRING)
    private HolidayType type;

    // Flags
    private Boolean isOptional = false; // optional holiday selection
    private Boolean isActive = true;
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
