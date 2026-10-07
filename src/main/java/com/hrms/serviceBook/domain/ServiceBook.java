package com.hrms.serviceBook.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
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


    @Column(nullable = false, unique = true, length = 50)
    private String serviceBookNo;

    private String serviceBookName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
   private Employee employee;

    private Boolean isActive = true;

    private Boolean isDeleted = false;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;


}