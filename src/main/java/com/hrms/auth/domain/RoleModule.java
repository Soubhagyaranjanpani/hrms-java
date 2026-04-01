package com.hrms.auth.domain;

import com.hrms.master.domain.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
public class RoleModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Role role;

    @ManyToOne
    private Module module;
}
