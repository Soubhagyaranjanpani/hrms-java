// path: src/main/java/com/hrms/Employee_Service_History/dto/EmployeeServiceHistoryDTO.java
package com.hrms.Employee_Service_History.dto;

import com.hrms.Timeline.dto.TimelineEventDTO;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeServiceHistoryDTO {

    private Long id;
    private String name;
    private String code;
    private String branch;
    private String department;
    private String designation;
    private LocalDate joiningDate;
    private LocalDate retirementDate;
    private String status;
    private String photo;

    private AppointmentDTO appointment;
    private ConfirmationDTO confirmation;
    private List<PromotionDTO> promotions;
    private List<TransferDTO> transfers;
    private List<DeputationDTO> deputations;
    private List<PayRevisionDTO> payRevisions;
    private List<TrainingDTO> training;
    private List<AwardDTO> awards;
    private List<DisciplinaryDTO> disciplinary;
    private List<QualificationDTO> qualifications;
    private List<CertificationDTO> certifications;

    private List<TimelineEventDTO> timeline;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AppointmentDTO {
        private String orderNo;
        private LocalDate appointmentDate;
        private String appointmentType;
        private String employmentType;
        private String initialDesignation;
        private LocalDate joiningDate;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ConfirmationDTO {
        private LocalDate confirmationDate;
        private String confirmationOrderNo;
        private String probationCompleted;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PromotionDTO {
        private LocalDate effectiveDate;
        private String from;
        private String to;
        private String orderNo;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TransferDTO {
        private LocalDate date;
        private String fromBranch;
        private String toBranch;
        private String reason;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DeputationDTO {
        private String organization;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PayRevisionDTO {
        private LocalDate effectiveDate;
        private Double oldBasic;
        private Double newBasic;
        private String orderNo;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TrainingDTO {
        private String trainingName;
        private String provider;
        private String type;
        private LocalDate startDate;
        private LocalDate endDate;
        private String certificate;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AwardDTO {
        private String awardName;
        private LocalDate date;
        private String issuedBy;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DisciplinaryDTO {
        private String caseNo;
        private String action;
        private String status;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class QualificationDTO {
        private String qualification;
        private String university;
        private String year;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CertificationDTO {
        private String certificate;
        private String issuedBy;
        private String validTill;
    }
}