package com.hrms.payrevision.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Master table for pay-revision reasons (e.g. "Annual Increment", "Promotion",
 * "Performance Based", "Market Correction") — mirrors how PromotionType is a
 * master table for Promotion, rather than a free-text field on the record.
 */
@Entity
@Getter
@Setter
@Table(name = "pay_revision_reasons")
public class PayRevisionReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
