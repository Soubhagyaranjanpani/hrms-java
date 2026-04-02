package com.hrms.attendance.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "attendance.cron")
@Getter
@Setter
public class AttendanceCronConfig {

    private String autoAbsent;
    private String autoCheckout;
}
