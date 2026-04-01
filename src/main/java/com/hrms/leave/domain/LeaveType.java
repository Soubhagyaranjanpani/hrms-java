package com.hrms.leave.domain;

import com.hrms.leave.domain.enums.LeaveExpiryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "leave_type")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // SICK, CASUAL, EARNED

    private Integer maxDaysPerYear;

    private Boolean carryForwardAllowed = false;

    private Integer maxCarryForwardDays;

    @Enumerated(EnumType.STRING)
    private LeaveExpiryType expiryType;
    // YEAR_END, FINANCIAL_YEAR_END, NONE

    private Boolean isActive = true;
}
