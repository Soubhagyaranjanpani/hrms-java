package com.hrms.promotion.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "promotion_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private Long oldBranchId;
    private Long newBranchId;

    private Long oldDepartmentId;
    private Long newDepartmentId;

    private Long oldDesignationId;
    private Long newDesignationId;

    @Column(unique = true)
    private String promotionOrderNumber;

    private LocalDate promotionDate;

    private Long promotionTypeId;

    private String oldGrade;
    private String newGrade;

    private LocalDate effectiveDate;

    private Long promotionAuthorityId;

    @Builder.Default
    private Boolean isDeleted = false;

    @Builder.Default
    private Integer flag = 1;
}