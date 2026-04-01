package com.hrms.employee.domain;

import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@Table(
        name = "employees",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"email"}),
                @UniqueConstraint(columnNames = {"employee_code"})
        }
)
public class Employee implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Business Identifier
    @Column(name = "employee_code", nullable = false, unique = true, length = 20)
    private String employeeCode;

    // Authentication
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // Role mapping
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Personal Info
    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    @Column(length = 15)
    private String phone;

    @Column(length = 500)
    private String address;

    private String profilePicture;

    // Organization mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    // Employment info
    private LocalDate joiningDate;

    private Boolean isActive = true;
    private Boolean isDeleted = false;

    // Security / login tracking
    private LocalDateTime lastLogin;

    private String tempOtp;
    private LocalDateTime otpExpiryTime;

    // Audit fields
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    // Lifecycle hooks
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.isDeleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // -------------------------------
    // Spring Security Implementation
    // -------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role != null) {
            return Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + role.getName())
            );
        }
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !Boolean.TRUE.equals(isDeleted);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !Boolean.TRUE.equals(isDeleted);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !Boolean.TRUE.equals(isDeleted);
    }

    @Override
    public boolean isEnabled() {
        return isActive && !Boolean.TRUE.equals(isDeleted);
    }
}
