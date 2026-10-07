package com.hrms.master.domain;

import lombok.Data;
import jakarta.persistence.*;  // For Spring Boot 3.x (Jakarta EE)
// OR use this for older Spring Boot versions:
// import javax.persistence.*;

@Entity
@Table(name = "deputation_type")
@Data
public class DeputationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "is_active")
    private Boolean isActive = true;
}