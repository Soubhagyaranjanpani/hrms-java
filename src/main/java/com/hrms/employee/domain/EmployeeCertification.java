package com.hrms.employee.domain;

import com.hrms.master.domain.Department;
import com.hrms.master.domain.Designation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
@Entity
@Table(name ="employee_certification")
@Getter
@Setter
public class EmployeeCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @Column(name = "certificate_name")
    private String certificateName;

    @Column (name = "issue_authority")
    private String issueAuthority;

    @Column (name = "certificate_number")
    private String certificateNumber;

    @Column (name = "issue_date")
    private LocalDate issueDate;

    @Column (name = "expiry_date")
    private LocalDate expiryDate;

    @Column (name = "expiry_reminder_days")
    private Integer expiryReminderDays;

    @Column (name = "file_path")
    private String filePath;

    @Column (name = "note_s")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="employee_id",referencedColumnName = "id")
    private Employee employee;

    @CreationTimestamp
    @Column (name = "created_date")
    private LocalDate createdDate;

    @Column (name = "updated_date")
    @UpdateTimestamp
    private LocalDate updatedDate;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name ="department_id",referencedColumnName = "id")
//    private Department department;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "designation_id",referencedColumnName = "id")
//    private Designation designation;


}
