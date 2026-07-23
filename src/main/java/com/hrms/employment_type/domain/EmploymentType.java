package com.hrms.employment_type.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employment_types")
@Data
public class EmploymentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    private Boolean isActive = true;
}