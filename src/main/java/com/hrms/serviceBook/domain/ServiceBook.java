package com.hrms.serviceBook.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_book")
@Data
public class ServiceBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;


    @Column(nullable = false, unique = true, length = 50)
    private String serviceBookNo;

    private String serviceBookName;

    private Boolean isActive = true;

    private Boolean isDeleted = false;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    @PrePersist
    public void prePersist() {
        this.isDeleted = false;
        this.isActive = true;
    }


}