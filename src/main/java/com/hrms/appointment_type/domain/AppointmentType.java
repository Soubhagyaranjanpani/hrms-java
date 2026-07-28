//package com.hrms.appointment_type.domain;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "appointment_type", schema = "public")
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class AppointmentType {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Long id;
//
//    @Column(name = "appointment_type", length = 100, nullable = false)
//    private String appointmentType;
//
//    @CreationTimestamp
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "created_by", length = 255)
//    private String createdBy;
//
//    @Column(name = "is_active", nullable = false)
//    @Builder.Default
//    private Boolean isActive = true;
//
//    @Column(name = "is_deleted", nullable = false)
//    @Builder.Default
//    private Boolean isDeleted = false;
//
//    @UpdateTimestamp
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @Column(name = "updated_by", length = 255)
//    private String updatedBy;
//}