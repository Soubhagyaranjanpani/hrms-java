package com.hrms.Recuirment.domain;

import com.hrms.master.domain.Branch;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="job_location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="location_code")
    private String locationCode;
    @Column(name="locationName")
    private String locationName;
    @Column(name="pin_code",nullable=false,length=6)
    private String pinCode;
    @Column(name="description",columnDefinition = "TEXT")
    private String description;

    // Branch
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    //Country
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="country_id",nullable = false)
    private Country country;

    //State
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="state_id",nullable = false)
    private State state;

    //City
    @ManyToOne(fetch=FetchType.LAZY,optional = false)
    @JoinColumn(name="city_id",nullable = false)
    private City city;

    //WorkMode
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "workmode_id",nullable = false)
    private WorkMode workMode;

    @Column(name="status")
    private String status;
    @CreationTimestamp
    @Column(name="created_at",updatable = false)
    private LocalDateTime createdAt;
    @Column(name="created_by")
    private String createdBy;

}
