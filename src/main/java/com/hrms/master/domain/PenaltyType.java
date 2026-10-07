package com.hrms.master.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "penalty_types")
@Data
public class PenaltyType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    private Boolean isActive = true;
}