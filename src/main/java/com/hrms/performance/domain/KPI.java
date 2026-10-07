package com.hrms.performance.domain;

import com.hrms.master.domain.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "kpi")
public class KPI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double weightage;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
