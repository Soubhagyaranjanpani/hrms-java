package com.hrms.Timeline.application;

import com.hrms.Awards_Recognition.domain.AwardRecord;

import com.hrms.Awards_Recognition.infrastructure.AwardRepository;
import com.hrms.Confirmation.domain.ConfirmationRecord;
import com.hrms.Confirmation.infrastructure.ConfirmationRepository;
import com.hrms.Timeline.dto.TimelineEventDTO;
import com.hrms.appointment.domain.AppointmentRecord;
import com.hrms.appointment.infrastructure.AppointmentRepository;
import com.hrms.deputation.domain.DeputationRecord;
import com.hrms.deputation.infrastructure.DeputationRepository;
import com.hrms.disciplinary.domain.DisciplinaryRecord;
import com.hrms.disciplinary.infrastructure.DisciplinaryRepository;
import com.hrms.payrevision.domain.PayRevisionRecord;
import com.hrms.payrevision.infrastructure.PayRevisionRepository;
import com.hrms.promotion.domain.PromotionRecord;
import com.hrms.promotion.infrastructure.PromotionRepository;
import com.hrms.retirement.domain.RetirementRecord;
import com.hrms.retirement.infrastructure.RetirementRepository;
import com.hrms.training.domain.TrainingRecord;
import com.hrms.training.infrastructure.TrainingRepository;
import com.hrms.transfer.domain.TransferRecord;
import com.hrms.transfer.infrastructure.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceBookTimelineService {

    private final AppointmentRepository appointmentRepo;
    private final ConfirmationRepository confirmationRepo;
    private final PromotionRepository promotionRepo;
    private final TransferRepository transferRepo;
    private final DeputationRepository deputationRepo;
    private final PayRevisionRepository payRevisionRepo;
    private final DisciplinaryRepository disciplinaryRepo;
    private final AwardRepository awardRepo;
    private final TrainingRepository trainingRepo;
    private final RetirementRepository retirementRepo;

    public List<TimelineEventDTO> getTimelineForEmployee(Long employeeId) {
        List<TimelineEventDTO> events = new ArrayList<>();

        appointmentRepo.findByEmployee_IdAndIsDeletedFalse(employeeId)
                .forEach(r -> events.add(mapAppointment(r)));

        confirmationRepo.findByEmployee_IdAndIsDeletedFalse(employeeId)
                .forEach(r -> events.add(mapConfirmation(r)));

        promotionRepo.findByEmployee_IdAndIsDeletedFalse(employeeId)
                .forEach(r -> events.add(mapPromotion(r)));

        transferRepo.findByEmployee_IdAndIsDeletedFalse(employeeId)
                .forEach(r -> events.add(mapTransfer(r)));

        deputationRepo.findByEmployee_IdAndIsDeletedFalse(employeeId)
                .forEach(r -> events.add(mapDeputation(r)));

        payRevisionRepo.findByEmployee_IdAndIsDeletedFalse(employeeId)
                .forEach(r -> events.add(mapPayRevision(r)));

        disciplinaryRepo.findByEmployee_IdAndIsDeletedFalse(employeeId)
                .forEach(r -> events.add(mapDisciplinary(r)));

        awardRepo.findByEmployee_IdAndIsDeletedFalse(employeeId)
                .forEach(r -> events.add(mapAward((AwardRecord) r)));

        trainingRepo.findByEmployee_IdAndIsDeletedFalse(employeeId)
                .forEach(r -> events.add(mapTraining((TrainingRecord) r)));

        retirementRepo.findByEmployee_IdAndIsDeletedFalse(employeeId)
                .forEach(r -> events.add(mapRetirement(r)));

        events.sort(Comparator.comparing(
                TimelineEventDTO::getDate,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        return events;
    }

    // ── Mappers ─────────────────────────────────────────

    private TimelineEventDTO mapAppointment(AppointmentRecord r) {
        String designation = r.getInitialDesignation() != null ? r.getInitialDesignation().getName() : null;
        String department = r.getInitialDepartment() != null ? r.getInitialDepartment().getName() : null;
        return TimelineEventDTO.builder()
                .id(r.getId())
                .type("appointment")
                .title("Appointment as " + safe(designation))
                .description("Appointed as " + safe(designation) + " in " + safe(department) + " department")
                .date(r.getAppointmentDate())
                .referenceNo(r.getAppointmentOrderNumber())
                .sourceModule("HRMS")
                .approvedBy(authorityName(r.getAppointmentAuthority()))
                .remarks(r.getRemarks())
                .department(department)
                .designation(designation)
                .build();
    }

    private TimelineEventDTO mapConfirmation(ConfirmationRecord r) {
        return TimelineEventDTO.builder()
                .id(r.getId())
                .type("confirmation")
                .title("Probation Confirmation")
                .description("Confirmed as " + safe(r.getDesignationName()) + " in " + safe(r.getDepartmentName()) + " department")
                .date(r.getConfirmationDate())
                .referenceNo(r.getConfirmationOrderNumber())
                .sourceModule("HRMS")
                .approvedBy(authorityName(r.getConfirmedBy()))
                .remarks(r.getRemarks())
                .department(r.getDepartmentName())
                .designation(r.getDesignationName())
                .build();
    }

    private TimelineEventDTO mapPromotion(PromotionRecord r) {
        String oldDesig = r.getOldDesignation() != null ? r.getOldDesignation().getName() : null;
        String newDesig = r.getNewDesignation() != null ? r.getNewDesignation().getName() : null;
        String newDept = r.getNewDepartment() != null ? r.getNewDepartment().getName() : null;
        return TimelineEventDTO.builder()
                .id(r.getId())
                .type("promotion")
                .title("Promotion to " + safe(newDesig))
                .description("Promoted from " + safe(oldDesig) + " to " + safe(newDesig))
                .date(r.getPromotionDate() != null ? r.getPromotionDate() : r.getEffectiveDate())
                .referenceNo(r.getPromotionOrderNumber())
                .sourceModule("HRMS")
                .approvedBy(authorityName(r.getPromotionAuthority()))
                .remarks(r.getRemarks())
                .department(newDept)
                .designation(newDesig)
                .build();
    }

    private TimelineEventDTO mapTransfer(TransferRecord r) {
        String fromBranch = r.getFromBranch() != null ? r.getFromBranch().getName() : null;
        String toBranch = r.getToBranch() != null ? r.getToBranch().getName() : null;
        String toDept = r.getToDepartment() != null ? r.getToDepartment().getName() : null;
        return TimelineEventDTO.builder()
                .id(r.getId())
                .type("transfer")
                .title("Transfer to " + safe(toBranch))
                .description("Transferred from " + safe(fromBranch) + " to " + safe(toBranch))
                .date(r.getTransferDate())
                .referenceNo(r.getTransferOrderNumber())
                .sourceModule("HRMS")
                .remarks(r.getTransferReason())
                .department(toDept)
                .designation(r.getDesignationName())
                .build();
    }

    private TimelineEventDTO mapDeputation(DeputationRecord r) {
        return TimelineEventDTO.builder()
                .id(r.getId())
                .type("deputation")
                .title("Deputation to " + safe(r.getDeputationOrganization()))
                .description("Deputed to " + safe(r.getDeputationOrganization())
                        + (r.getEndDate() != null ? " until " + r.getEndDate() : ""))
                .date(r.getStartDate())
                .referenceNo(r.getDeputationOrderNumber())
                .sourceModule("HRMS")
                .approvedBy(authorityName(r.getReportingAuthority()))
                .remarks(r.getRemarks())
                .department(r.getDepartmentName())
                .designation(r.getDesignationName())
                .build();
    }

    private TimelineEventDTO mapPayRevision(PayRevisionRecord r) {
        String reason = r.getReason() != null ? r.getReason().getName() : null;
        return TimelineEventDTO.builder()
                .id(r.getId())
                .type("payRevision")
                .title("Pay Revision" + (reason != null ? " (" + reason + ")" : ""))
                .description("Pay scale revised from "
                        + safe(r.getPreviousPayScaleMin()) + "-" + safe(r.getPreviousPayScaleMax())
                        + " to " + safe(r.getRevisedPayScaleMin()) + "-" + safe(r.getRevisedPayScaleMax()))
                .date(r.getEffectiveDate())
                .referenceNo(r.getPayRevisionOrderNumber())
                .sourceModule("Payroll")
                .remarks(r.getRemarks())
                .build();
    }

    private TimelineEventDTO mapDisciplinary(DisciplinaryRecord r) {
        String action = r.getActionType() != null ? r.getActionType().getName() : "Disciplinary Action";
        String penalty = r.getPenaltyType() != null ? r.getPenaltyType().getName() : null;
        return TimelineEventDTO.builder()
                .id(r.getId())
                .type("disciplinary")
                .title(action)
                .description("Case " + safe(r.getCaseNumber())
                        + (penalty != null ? " — Penalty: " + penalty : ""))
                .date(r.getIncidentDate())
                .referenceNo(r.getCaseNumber())
                .sourceModule("HRMS")
                .approvedBy(authorityName(r.getInvestigationOfficer()))
                .remarks(r.getRemarks())
                .department(r.getDepartmentName())
                .designation(r.getDesignationName())
                .build();
    }

    private TimelineEventDTO mapAward(AwardRecord r) {
        String awardType = r.getAwardType() != null ? r.getAwardType().getName() : null;
        return TimelineEventDTO.builder()
                .id(r.getId())
                .type("award")
                .title(safe(r.getAwardName()))
                .description(r.getDescription() != null ? r.getDescription()
                        : "Received " + safe(r.getAwardName()) + (awardType != null ? " (" + awardType + ")" : ""))
                .date(r.getAwardDate())
                .referenceNo("AWD-" + r.getId())
                .sourceModule("HRMS")
                .approvedBy(authorityName(r.getIssuedBy()))
                .department(r.getDepartmentName())
                .designation(r.getDesignationName())
                .build();
    }

    private TimelineEventDTO mapTraining(TrainingRecord r) {
        return TimelineEventDTO.builder()
                .id(r.getId())
                .type("training")
                .title(r.getTrainingName())
                .description("Training by " + safe(r.getProvider())
                        + (r.getHours() != null ? " — " + r.getHours() + " hrs" : "")
                        + (r.getCertification() != null ? " — Certification: " + r.getCertification() : ""))
                .date(r.getStartDate())
                .referenceNo("TRN-" + r.getId())
                .sourceModule("Training")
                .department(r.getDepartmentName())
                .designation(r.getDesignationName())
                .build();
    }

    private TimelineEventDTO mapRetirement(RetirementRecord r) {
        String type = r.getRetirementType() != null ? r.getRetirementType().getName() : "Retirement";
        return TimelineEventDTO.builder()
                .id(r.getId())
                .type("retirement")
                .title(type)
                .description(r.getRetirementBenefits() != null ? r.getRetirementBenefits() : type)
                .date(r.getRetirementDate())
                .referenceNo(r.getRetirementOrder())
                .sourceModule("HRMS")
                .department(r.getDepartmentName())
                .designation(r.getDesignationName())
                .build();
    }

    // ── Helpers ─────────────────────────────────────────

    private String safe(Object v) {
        return v == null ? "" : v.toString();
    }

    /**
     * Adjust to your real EmployeeDesignation getters — assuming it wraps an Employee.
     */
    private String authorityName(Object employeeDesignation) {
        if (employeeDesignation == null) return null;
        try {
            var getEmployee = employeeDesignation.getClass().getMethod("getEmployee");
            Object employee = getEmployee.invoke(employeeDesignation);
            if (employee == null) return null;
            var getName = employee.getClass().getMethod("getFullName");
            return (String) getName.invoke(employee);
        } catch (Exception e) {
            return null;
        }
    }
}