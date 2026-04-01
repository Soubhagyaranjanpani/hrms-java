package com.hrms.master.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "designations")
@Data
public class Designation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    private Boolean isActive = true;
}
