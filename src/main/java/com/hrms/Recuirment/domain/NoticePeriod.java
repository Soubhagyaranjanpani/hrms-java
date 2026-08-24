package com.hrms.Recuirment.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.time.LocalDateTime;

@Entity
@Table(name="notice_period")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class NoticePeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="period_code",nullable = false,unique = true)
    private String periodCode;
    @Column(name="period_name",nullable = false)
    private String periodName;
    @Column(name="days")
    private Integer days;
    @Column(name="description")
    private String description;
    @Column(name="status")
    private String status;
    @CreationTimestamp
    @Column(name="created_at",updatable = false)
    private LocalDateTime createdAt;
    @Column(name="created_by")
    private String createdBy;
}
