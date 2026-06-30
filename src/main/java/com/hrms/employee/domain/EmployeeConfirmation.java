package com.hrms.employee.domain;

import com.hrms.appointment_type.domain.AppointmentAuthority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employee_confirmation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "confirmation_order_number", nullable = false, unique = true)
    private String confirmationOrderNumber;

    @Column(name = "confirmation_date", nullable = false)
    private LocalDate confirmationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by", referencedColumnName = "id")
    private AppointmentAuthority confirmedBy;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    // Optional field (can be used later for file path)
    @Column(name = "document", length = 500)
    private String document;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}