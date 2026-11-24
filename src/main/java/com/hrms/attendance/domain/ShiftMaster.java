package com.hrms.attendance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "shift_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(length = 20)
    private String shortCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Double durationHours;

    private Integer breakMinutes;

    private Integer graceMinutes;

    @Builder.Default
    private Boolean isNightShift = false;

    private String color;

    private String bgColor;

    private String borderColor;

    @Builder.Default
    private Boolean isActive = true;

    private String description;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
        if (this.isNightShift == null) this.isNightShift = false;
        if (this.durationHours == null) this.durationHours = 8.0;
        if (this.shortCode == null && this.code != null) {
            this.shortCode = this.code.length() > 5 ? this.code.substring(0, 5) : this.code;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
