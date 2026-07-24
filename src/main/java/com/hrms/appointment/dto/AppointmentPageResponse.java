package com.hrms.appointment.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AppointmentPageResponse {
    private List<AppointmentRecordResponse> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
