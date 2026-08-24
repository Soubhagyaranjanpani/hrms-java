package com.hrms.Recuirment.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="offer_status")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferStatus {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="status_code",nullable = false,unique = true)
    private String statusCode;

    @Column(name="status_name",nullable = false,length=100)
    private String statusName;

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
