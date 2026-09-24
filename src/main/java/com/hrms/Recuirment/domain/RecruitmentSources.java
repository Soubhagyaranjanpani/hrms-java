package com.hrms.Recuirment.domain;

import com.hrms.master.domain.SourceCategory;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class RecruitmentSources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourceCode;

    private String sourceName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private SourceCategory category;

    private String description;

    private Integer displaySequence;

    private Boolean status;

    private LocalDateTime createdAt;

    private String createdBy;


    // =========================
    // ID
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    // =========================
    // SOURCE CODE
    // =========================

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }


    // =========================
    // SOURCE NAME
    // =========================

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }


    // =========================
    // CATEGORY
    // =========================

    public SourceCategory getCategory() {
        return category;
    }

    public void setCategory(SourceCategory category) {
        this.category = category;
    }


    // =========================
    // DESCRIPTION
    // =========================

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    // =========================
    // DISPLAY SEQUENCE
    // =========================

    public Integer getDisplaySequence() {
        return displaySequence;
    }

    public void setDisplaySequence(Integer displaySequence) {
        this.displaySequence = displaySequence;
    }


    // =========================
    // STATUS
    // =========================

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


    // =========================
    // CREATED AT
    // =========================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // =========================
    // CREATED BY
    // =========================

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}