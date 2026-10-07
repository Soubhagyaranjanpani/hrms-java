package com.hrms.master.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pension_eligibility")
@Data
public class PensionEligibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "is_active")
    private Boolean isActive = true;
}