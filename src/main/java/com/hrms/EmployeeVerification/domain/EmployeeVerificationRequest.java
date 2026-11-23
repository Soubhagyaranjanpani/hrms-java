
package com.hrms.EmployeeVerification.domain;

import com.hrms.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_verification_requests")
@Getter
@Setter
public class EmployeeVerificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private String clientName;

    private String projectName;

    @Column(nullable = false)
    private String siteLocation;

    private String contactPerson;

    @Column(columnDefinition = "TEXT")
    private String purposeDescription;

    @Column(columnDefinition = "TEXT")
    private String datesJson;

    private Integer totalDays;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status =
            VerificationStatus.PENDING_VERIFICATION;

    private String rejectionReason;
    private String reviewedBy;
    private LocalDateTime reviewedAt;

    private LocalDateTime createdAt = LocalDateTime.now();

    private Boolean isDeleted = false;
}
