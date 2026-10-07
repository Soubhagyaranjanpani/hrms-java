package com.hrms.service_history.domain;

import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.Designation;
import com.hrms.serviceBook.domain.ServiceBook;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_history")
@Data
public class ServiceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ServiceBook ke sath relationship (Many History -> One ServiceBook)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_book_id", nullable = false)
    private ServiceBook serviceBook;



    private String eventType;      // e.g. Promotion, Transfer, Increment, Suspension

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_designation_id")
    private Designation fromDesignation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_designation_id", nullable = false)
    private Designation toDesignation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "from_Branch_id")
    private Branch fromBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "to_Branch_id", nullable = false)
    private  Branch toBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "from_Department_id")
    private Department fromDepartment;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "to_Department_id", nullable = false)
    private Department toDepartment;


    private LocalDate formDate;

    private LocalDate  toDate;


    private String remarks;

    private Boolean isActive = true;
    private Boolean isDeleted = false;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        this.isDeleted = false;
        this.isActive = true;
    }
}