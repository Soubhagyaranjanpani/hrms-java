package com.hrms.Recuirment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewPanelResponse {
    private Long id;
    private String panelCode;
    private String panelName;
    private String status;
    private String lastChangeBy;
    private LocalDateTime lastChangeAt;
    private Long departmentId;
    private Long primaryInterviewerId;
    private Long secondaryInterviewerId;
    private List<Long> panelMembersIds;
}
