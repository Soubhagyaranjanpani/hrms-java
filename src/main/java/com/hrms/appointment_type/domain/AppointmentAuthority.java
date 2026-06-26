package com.hrms.appointment_type.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "appointment_authority")
public class AppointmentAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authorityName;
    private String department;
    private String email;

    @Column(nullable = false)
    private Integer flag = 0;

    public AppointmentAuthority() {
    }

    public AppointmentAuthority(Long id, String authorityName,
                                String department, String email,
                                Integer flag) {
        this.id = id;
        this.authorityName = authorityName;
        this.department = department;
        this.email = email;
        this.flag = flag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }


}
