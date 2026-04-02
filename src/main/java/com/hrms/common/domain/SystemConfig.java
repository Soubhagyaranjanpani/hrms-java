package com.hrms.common.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Data;

@Entity
@Data
public class SystemConfig {

    @Id
    @Column(nullable = false, unique = true)
    private String key;

    @Column(nullable = false)
    private String value;
}
//('ATTENDANCE_GRACE_MINUTES', '15'),
//        ('MAX_LEAVE_DAYS', '30'),
//        ('SANDWICH_POLICY', 'true');
