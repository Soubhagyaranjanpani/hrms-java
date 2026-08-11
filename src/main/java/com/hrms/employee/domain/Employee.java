package com.hrms.employee.domain;

import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.Designation;
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
import java.util.List;

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

    @Column(name = "employee_code", nullable = false, unique = true, length = 20)
    private String employeeCode;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    @Column(length = 15)
    private String phone;

    @Column(length = 500)
    private String address;

    private String profilePicture;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    private EmployeeGrade grade;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "designation_id")
    private Designation designation;

    @OneToMany(mappedBy = "manager")
    private List<Employee> subordinates;

    private LocalDate joiningDate;

    private Boolean isActive = true;
    private Boolean isDeleted = false;

    // ✅ is_retirement: false = Active (default), true = Retired
    @Column(name = "is_retirement", nullable = false)
    private Boolean isRetirement = false;

    private LocalDateTime lastLogin;

    private String tempOtp;
    private LocalDateTime otpExpiryTime;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    @Column(name = "bank_account", length = 20)
    private String bankAccount;

    @Column(name = "uan", length = 12)
    private String uan;

    @Column(name = "pan", length = 10)
    private String pan;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.isDeleted = false;
        if (this.isRetirement == null) {
            this.isRetirement= false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role != null) {
            return Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + role.getName())
            );
        }
        return Collections.emptyList();
    }

    public String getFullName() {
        if (lastName == null || lastName.trim().isEmpty()) {
            return firstName;
        }
        return firstName + " " + lastName;
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