package com.hrms.Recuirment.dto;

import lombok.Data;

import java.util.List;


@Data
public class InterviewPanelCreateReq  {
    private String panelCode;
    private String panelName;
    private String status;
    private Long departmentId;
    private Long primaryInterviewerId;
    private Long secondaryInterviewerId;
    private List<Long> panelMembersIds;
}
