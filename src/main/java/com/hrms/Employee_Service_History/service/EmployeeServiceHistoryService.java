//package com.hrms.Employee_Service_History.service;
//
//import com.hrms.Employee_Service_History.dto.EmployeeServiceHistoryDTO;
//import com.hrms.Employee_Service_History.dto.EmployeeServiceHistoryDTO.*;
//import com.hrms.Timeline.dto.TimelineEventDTO;
//
//import com.hrms.employee.domain.Employee;
//import com.hrms.employee.domain.EmployeeQualification;
//import com.hrms.employee.domain.EmployeeCertification;
//import com.hrms.employee.infrastructure.EmployeeRepository;
//import com.hrms.employee.infrastructure.EmployeeQualificationRepository;
//import com.hrms.employee.infrastructure.EmployeeCertificationRepository;
//
//import com.hrms.appointment.domain.AppointmentRecord;
//import com.hrms.appointment.infrastructure.AppointmentRepository;
//import com.hrms.Confirmation.domain.ConfirmationRecord;
//import com.hrms.Confirmation.infrastructure.ConfirmationRepository;
//import com.hrms.promotion.domain.PromotionRecord;
//import com.hrms.promotion.infrastructure.PromotionRepository;
//import com.hrms.transfer.domain.TransferRecord;
//import com.hrms.transfer.infrastructure.TransferRepository;
//import com.hrms.deputation.domain.DeputationRecord;
//import com.hrms.deputation.infrastructure.DeputationRepository;
//import com.hrms.payrevision.domain.PayRevisionRecord;
//import com.hrms.payrevision.infrastructure.PayRevisionRepository;
//import com.hrms.disciplinary.domain.DisciplinaryRecord;
//import com.hrms.disciplinary.infrastructure.DisciplinaryRepository;
//import com.hrms.Awards_Recognition.domain.AwardRecord;
//import com.hrms.Awards_Recognition.infrastructure.AwardRepository;
//import com.hrms.training.domain.TrainingRecord;
//import com.hrms.training.infrastructure.TrainingRepository;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class EmployeeServiceHistoryService {
//
//    private final EmployeeRepository employeeRepository;
//    private final AppointmentRepository appointmentRepo;
//    private final ConfirmationRepository confirmationRepo;
//    private final PromotionRepository promotionRepo;
//    private final TransferRepository transferRepo;
//    private final DeputationRepository deputationRepo;
//    private final PayRevisionRepository payRevisionRepo;
//    private final DisciplinaryRepository disciplinaryRepo;
//    private final AwardRepository awardRepo;
//    private final TrainingRepository trainingRepo;
//    private final EmployeeQualificationRepository qualificationRepo;
//    private final EmployeeCertificationRepository certificationRepo;
//
//    public EmployeeServiceHistoryDTO getHistory(Long employeeId) {
//
//        Employee emp = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));
//
//        List<AppointmentRecord> appointments = appointmentRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
//        AppointmentDTO appointmentDTO = appointments.isEmpty() ? null : mapAppointment(appointments.get(0));
//
//        List<ConfirmationRecord> confirmations = confirmationRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
//        ConfirmationDTO confirmationDTO = confirmations.isEmpty() ? null : mapConfirmation(confirmations.get(0));
//
//        List<PromotionRecord> promotionRecords = promotionRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
//        List<PromotionDTO> promotions = promotionRecords.stream().map(this::mapPromotion).collect(Collectors.toList());
//
//        List<TransferRecord> transferRecords = transferRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
//        List<TransferDTO> transfers = transferRecords.stream().map(this::mapTransfer).collect(Collectors.toList());
//
//        List<DeputationRecord> deputationRecords = deputationRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
//        List<DeputationDTO> deputations = deputationRecords.stream().map(this::mapDeputation).collect(Collectors.toList());
//
//        List<PayRevisionRecord> payRevisionRecords = payRevisionRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
//        List<PayRevisionDTO> payRevisions = payRevisionRecords.stream().map(this::mapPayRevision).collect(Collectors.toList());
//
//        List<TrainingRecord> trainingRecords = trainingRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
//        List<TrainingDTO> trainings = trainingRecords.stream().map(this::mapTraining).collect(Collectors.toList());
//
//        List<AwardRecord> awardRecords = awardRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
//        List<AwardDTO> awards = awardRecords.stream().map(this::mapAward).collect(Collectors.toList());
//
//        List<DisciplinaryRecord> disciplinaryRecords = disciplinaryRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
//        List<DisciplinaryDTO> disciplinary = disciplinaryRecords.stream().map(this::mapDisciplinary).collect(Collectors.toList());
//
//        List<QualificationDTO> qualifications = qualificationRepo.findByEmployee_Id(employeeId)
//                .stream().map(this::mapQualification).collect(Collectors.toList());
//
//        List<CertificationDTO> certifications = certificationRepo.findByEmployee_Id(employeeId)
//                .stream().map(this::mapCertification).collect(Collectors.toList());
//
//        List<TimelineEventDTO> timeline = buildTimeline(
//                appointments, confirmations, promotionRecords, transferRecords,
//                deputationRecords, payRevisionRecords, disciplinaryRecords,
//                awardRecords, trainingRecords);
//
//        return EmployeeServiceHistoryDTO.builder()
//                .id(emp.getId())
//                .name(emp.getFullName())
//                .code(emp.getEmployeeCode())
//                .branch(emp.getBranch() != null ? emp.getBranch().getName() : null)
//                .department(emp.getDepartment() != null ? emp.getDepartment().getName() : null)
//                .designation(emp.getDesignation() != null ? emp.getDesignation().getName() : null)
//                .joiningDate(emp.getJoiningDate())
//                .status(Boolean.TRUE.equals(emp.getIsRetirement()) ? "Retired" : "Active")
//                .photo(emp.getProfilePicture())
//                .appointment(appointmentDTO)
//                .confirmation(confirmationDTO)
//                .promotions(promotions)
//                .transfers(transfers)
//                .deputations(deputations)
//                .payRevisions(payRevisions)
//                .training(trainings)
//                .awards(awards)
//                .disciplinary(disciplinary)
//                .qualifications(qualifications)
//                .certifications(certifications)
//                .timeline(timeline)
//                .build();
//    }
//
//    // ── Mappers ──────────────────────────────────────────
//
//    private AppointmentDTO mapAppointment(AppointmentRecord r) {
//        return AppointmentDTO.builder()
//                .orderNo(r.getAppointmentOrderNumber())
//                .appointmentDate(r.getAppointmentDate())
//                .appointmentType(r.getAppointmentType() != null ? r.getAppointmentType().getName() : null)
//                .employmentType(r.getEmploymentType() != null ? r.getEmploymentType().getName() : null)
//                .initialDesignation(r.getInitialDesignation() != null ? r.getInitialDesignation().getName() : null)
//                .joiningDate(r.getJoiningDate())
//                .build();
//    }
//
//    private ConfirmationDTO mapConfirmation(ConfirmationRecord r) {
//        return ConfirmationDTO.builder()
//                .confirmationDate(r.getConfirmationDate())
//                .confirmationOrderNo(r.getConfirmationOrderNumber())
//                .probationCompleted("Yes")
//                .build();
//    }
//
//    private PromotionDTO mapPromotion(PromotionRecord r) {
//        return PromotionDTO.builder()
//                .effectiveDate(r.getEffectiveDate() != null ? r.getEffectiveDate() : r.getPromotionDate())
//                .from(r.getOldDesignation() != null ? r.getOldDesignation().getName() : null)
//                .to(r.getNewDesignation() != null ? r.getNewDesignation().getName() : null)
//                .orderNo(r.getPromotionOrderNumber())
//                .build();
//    }
//
//    private TransferDTO mapTransfer(TransferRecord r) {
//        return TransferDTO.builder()
//                .date(r.getTransferDate())
//                .fromBranch(r.getFromBranch() != null ? r.getFromBranch().getName() : null)
//                .toBranch(r.getToBranch() != null ? r.getToBranch().getName() : null)
//                .reason(r.getTransferReason())
//                .build();
//    }
//
//    private DeputationDTO mapDeputation(DeputationRecord r) {
//        String status = r.getEndDate() != null && r.getEndDate().isBefore(LocalDate.now())
//                ? "Completed" : "Ongoing";
//        return DeputationDTO.builder()
//                .organization(r.getDeputationOrganization())
//                .startDate(r.getStartDate())
//                .endDate(r.getEndDate())
//                .status(status)
//                .build();
//    }
//
//    private PayRevisionDTO mapPayRevision(PayRevisionRecord r) {
//        return PayRevisionDTO.builder()
//                .effectiveDate(r.getEffectiveDate())
//                .oldBasic(r.getPreviousPayScaleMin())
//                .newBasic(r.getRevisedPayScaleMin())
//                .orderNo(r.getPayRevisionOrderNumber())
//                .build();
//    }
//
//    private TrainingDTO mapTraining(TrainingRecord r) {
//        return TrainingDTO.builder()
//                .trainingName(r.getTrainingName())
//                .provider(r.getProvider())
//                .type("Technical")
//                .startDate(r.getStartDate())
//                .endDate(r.getEndDate())
//                .certificate(r.getDocumentName())
//                .build();
//    }
//
//    private AwardDTO mapAward(AwardRecord r) {
//        return AwardDTO.builder()
//                .awardName(r.getAwardName())
//                .date(r.getAwardDate())
//                .issuedBy(authorityName(r.getIssuedBy()))
//                .build();
//    }
//
//    private DisciplinaryDTO mapDisciplinary(DisciplinaryRecord r) {
//        return DisciplinaryDTO.builder()
//                .caseNo(r.getCaseNumber())
//                .action(r.getActionType() != null ? r.getActionType().getName() : null)
//                .status(r.getResolutionDate() != null ? "Closed" : "Pending")
//                .build();
//    }
//
//    private QualificationDTO mapQualification(EmployeeQualification q) {
//        return QualificationDTO.builder()
//                .qualification(q.getDegree())
//                .university(q.getInstitution())
//                .year(q.getYear() != null ? q.getYear().toString() : null)
//                .build();
//    }
//
//    private CertificationDTO mapCertification(EmployeeCertification c) {
//        return CertificationDTO.builder()
//                .certificate(c.getCertificateName())
//                .issuedBy(c.getIssueAuthority())
//                .validTill(c.getExpiryDate() != null ? c.getExpiryDate().toString() : null)
//                .build();
//    }
//
//    private String authorityName(Object employeeDesignation) {
//        if (employeeDesignation == null) return null;
//        try {
//            var getEmployee = employeeDesignation.getClass().getMethod("getEmployee");
//            Object employee = getEmployee.invoke(employeeDesignation);
//            if (employee == null) return null;
//            var getName = employee.getClass().getMethod("getFullName");
//            return (String) getName.invoke(employee);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    // ── Combined Timeline ──────────────────────────────────
//    private List<TimelineEventDTO> buildTimeline(
//            List<AppointmentRecord> appointments, List<ConfirmationRecord> confirmations,
//            List<PromotionRecord> promotions, List<TransferRecord> transfers,
//            List<DeputationRecord> deputations, List<PayRevisionRecord> payRevisions,
//            List<DisciplinaryRecord> disciplinaries, List<AwardRecord> awards,
//            List<TrainingRecord> trainings) {
//
//        List<TimelineEventDTO> events = new ArrayList<>();
//
//        appointments.forEach(r -> events.add(TimelineEventDTO.builder()
//                .id(r.getId()).type("appointment")
//                .title("Appointment - " + safe(r.getAppointmentOrderNumber()))
//                .date(r.getAppointmentDate()).referenceNo(r.getAppointmentOrderNumber())
//                .sourceModule("HRMS").remarks(r.getRemarks()).build()));
//
//        confirmations.forEach(r -> events.add(TimelineEventDTO.builder()
//                .id(r.getId()).type("confirmation")
//                .title("Confirmation - " + safe(r.getConfirmationOrderNumber()))
//                .date(r.getConfirmationDate()).referenceNo(r.getConfirmationOrderNumber())
//                .sourceModule("HRMS").remarks(r.getRemarks()).build()));
//
//        promotions.forEach(r -> events.add(TimelineEventDTO.builder()
//                .id(r.getId()).type("promotion")
//                .title("Promotion - " + safe(r.getPromotionOrderNumber()))
//                .date(r.getEffectiveDate() != null ? r.getEffectiveDate() : r.getPromotionDate())
//                .referenceNo(r.getPromotionOrderNumber())
//                .sourceModule("HRMS").remarks(r.getRemarks()).build()));
//
//        transfers.forEach(r -> events.add(TimelineEventDTO.builder()
//                .id(r.getId()).type("transfer")
//                .title("Transfer - " + safe(r.getTransferOrderNumber()))
//                .date(r.getTransferDate()).referenceNo(r.getTransferOrderNumber())
//                .sourceModule("HRMS").remarks(r.getTransferReason()).build()));
//
//        deputations.forEach(r -> events.add(TimelineEventDTO.builder()
//                .id(r.getId()).type("deputation")
//                .title("Deputation - " + safe(r.getDeputationOrganization()))
//                .date(r.getStartDate()).referenceNo(r.getDeputationOrderNumber())
//                .sourceModule("HRMS").remarks(r.getRemarks()).build()));
//
//        payRevisions.forEach(r -> events.add(TimelineEventDTO.builder()
//                .id(r.getId()).type("payRevision")
//                .title("Pay Revision - " + safe(r.getPayRevisionOrderNumber()))
//                .date(r.getEffectiveDate()).referenceNo(r.getPayRevisionOrderNumber())
//                .sourceModule("Payroll").remarks(r.getRemarks()).build()));
//
//        disciplinaries.forEach(r -> events.add(TimelineEventDTO.builder()
//                .id(r.getId()).type("disciplinary")
//                .title("Disciplinary - " + safe(r.getCaseNumber()))
//                .date(r.getIncidentDate()).referenceNo(r.getCaseNumber())
//                .sourceModule("HRMS").remarks(r.getRemarks()).build()));
//
//        awards.forEach(r -> events.add(TimelineEventDTO.builder()
//                .id(r.getId()).type("award")
//                .title(safe(r.getAwardName()))
//                .date(r.getAwardDate()).referenceNo("AWD-" + r.getId())
//                .sourceModule("HRMS").remarks(r.getDescription()).build()));
//
//        trainings.forEach(r -> events.add(TimelineEventDTO.builder()
//                .id(r.getId()).type("training")
//                .title(safe(r.getTrainingName()))
//                .date(r.getStartDate()).referenceNo("TRN-" + r.getId())
//                .sourceModule("Training").build()));
//
//        events.sort(Comparator.comparing(TimelineEventDTO::getDate,
//                Comparator.nullsLast(Comparator.reverseOrder())));
//
//        return events;
//    }
//
//    private String safe(Object v) {
//        return v == null ? "" : v.toString();
//    }
//}




package com.hrms.Employee_Service_History.service;

import com.hrms.Employee_Service_History.dto.EmployeeServiceHistoryDTO;
import com.hrms.Employee_Service_History.dto.EmployeeServiceHistoryDTO.*;
import com.hrms.Timeline.dto.TimelineEventDTO;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeQualification;
import com.hrms.employee.domain.EmployeeCertification;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.employee.infrastructure.EmployeeQualificationRepository;
import com.hrms.employee.infrastructure.EmployeeCertificationRepository;

import com.hrms.appointment.domain.AppointmentRecord;
import com.hrms.appointment.infrastructure.AppointmentRepository;
import com.hrms.Confirmation.domain.ConfirmationRecord;
import com.hrms.Confirmation.infrastructure.ConfirmationRepository;
import com.hrms.promotion.domain.PromotionRecord;
import com.hrms.promotion.infrastructure.PromotionRepository;
import com.hrms.transfer.domain.TransferRecord;
import com.hrms.transfer.infrastructure.TransferRepository;
import com.hrms.deputation.domain.DeputationRecord;
import com.hrms.deputation.infrastructure.DeputationRepository;
import com.hrms.payrevision.domain.PayRevisionRecord;
import com.hrms.payrevision.infrastructure.PayRevisionRepository;
import com.hrms.disciplinary.domain.DisciplinaryRecord;
import com.hrms.disciplinary.infrastructure.DisciplinaryRepository;
import com.hrms.Awards_Recognition.domain.AwardRecord;
import com.hrms.Awards_Recognition.infrastructure.AwardRepository;
import com.hrms.training.domain.TrainingRecord;
import com.hrms.training.infrastructure.TrainingRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceHistoryService {

    private final EmployeeRepository employeeRepository;
    private final AppointmentRepository appointmentRepo;
    private final ConfirmationRepository confirmationRepo;
    private final PromotionRepository promotionRepo;
    private final TransferRepository transferRepo;
    private final DeputationRepository deputationRepo;
    private final PayRevisionRepository payRevisionRepo;
    private final DisciplinaryRepository disciplinaryRepo;
    private final AwardRepository awardRepo;
    private final TrainingRepository trainingRepo;
    private final EmployeeQualificationRepository qualificationRepo;
    private final EmployeeCertificationRepository certificationRepo;

    public EmployeeServiceHistoryDTO getHistory(Long employeeId) {

        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        List<AppointmentRecord> appointments = appointmentRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
        AppointmentDTO appointmentDTO = appointments.isEmpty() ? null : mapAppointment(appointments.get(0));

        List<ConfirmationRecord> confirmations = confirmationRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
        ConfirmationDTO confirmationDTO = confirmations.isEmpty() ? null : mapConfirmation(confirmations.get(0));

        List<PromotionRecord> promotionRecords = promotionRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
        List<PromotionDTO> promotions = promotionRecords.stream().map(this::mapPromotion).collect(Collectors.toList());

        List<TransferRecord> transferRecords = transferRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
        List<TransferDTO> transfers = transferRecords.stream().map(this::mapTransfer).collect(Collectors.toList());

        List<DeputationRecord> deputationRecords = deputationRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
        List<DeputationDTO> deputations = deputationRecords.stream().map(this::mapDeputation).collect(Collectors.toList());

        List<PayRevisionRecord> payRevisionRecords = payRevisionRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
        List<PayRevisionDTO> payRevisions = payRevisionRecords.stream().map(this::mapPayRevision).collect(Collectors.toList());

        List<TrainingRecord> trainingRecords = trainingRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
        List<TrainingDTO> trainings = trainingRecords.stream().map(this::mapTraining).collect(Collectors.toList());

        List<AwardRecord> awardRecords = awardRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
        List<AwardDTO> awards = awardRecords.stream().map(this::mapAward).collect(Collectors.toList());

        List<DisciplinaryRecord> disciplinaryRecords = disciplinaryRepo.findByEmployee_IdAndIsDeletedFalse(employeeId);
        List<DisciplinaryDTO> disciplinary = disciplinaryRecords.stream().map(this::mapDisciplinary).collect(Collectors.toList());

        List<QualificationDTO> qualifications = qualificationRepo.findByEmployee_Id(employeeId)
                .stream().map(this::mapQualification).collect(Collectors.toList());

        List<CertificationDTO> certifications = certificationRepo.findByEmployee_Id(employeeId)
                .stream().map(this::mapCertification).collect(Collectors.toList());

        List<TimelineEventDTO> timeline = buildTimeline(
                appointments, confirmations, promotionRecords, transferRecords,
                deputationRecords, payRevisionRecords, disciplinaryRecords,
                awardRecords, trainingRecords);

        return EmployeeServiceHistoryDTO.builder()
                .id(emp.getId())
                .name(emp.getFullName())
                .code(emp.getEmployeeCode())
                .branch(emp.getBranch() != null ? emp.getBranch().getName() : null)
                .department(emp.getDepartment() != null ? emp.getDepartment().getName() : null)
                .designation(emp.getDesignation() != null ? emp.getDesignation().getName() : null)
                .joiningDate(emp.getJoiningDate())
                .status(Boolean.TRUE.equals(emp.getIsRetirement()) ? "Retired" : "Active")
                .photo(emp.getProfilePicture())
                .appointment(appointmentDTO)
                .confirmation(confirmationDTO)
                .promotions(promotions)
                .transfers(transfers)
                .deputations(deputations)
                .payRevisions(payRevisions)
                .training(trainings)
                .awards(awards)
                .disciplinary(disciplinary)
                .qualifications(qualifications)
                .certifications(certifications)
                .timeline(timeline)
                .build();
    }

    // ── Mappers ──────────────────────────────────────────

    private AppointmentDTO mapAppointment(AppointmentRecord r) {
        return AppointmentDTO.builder()
                .orderNo(r.getAppointmentOrderNumber())
                .appointmentDate(r.getAppointmentDate())
                .appointmentType(r.getAppointmentType() != null ? r.getAppointmentType().getName() : null)
                .employmentType(r.getEmploymentType() != null ? r.getEmploymentType().getName() : null)
                .initialDesignation(r.getInitialDesignation() != null ? r.getInitialDesignation().getName() : null)
                .joiningDate(r.getJoiningDate())
                .build();
    }

    private ConfirmationDTO mapConfirmation(ConfirmationRecord r) {
        return ConfirmationDTO.builder()
                .confirmationDate(r.getConfirmationDate())
                .confirmationOrderNo(r.getConfirmationOrderNumber())
                .probationCompleted("Yes")
                .build();
    }

    private PromotionDTO mapPromotion(PromotionRecord r) {
        return PromotionDTO.builder()
                .effectiveDate(r.getEffectiveDate() != null ? r.getEffectiveDate() : r.getPromotionDate())
                .from(r.getOldDesignation() != null ? r.getOldDesignation().getName() : null)
                .to(r.getNewDesignation() != null ? r.getNewDesignation().getName() : null)
                .orderNo(r.getPromotionOrderNumber())
                .build();
    }

    private TransferDTO mapTransfer(TransferRecord r) {
        return TransferDTO.builder()
                .date(r.getTransferDate())
                .fromBranch(r.getFromBranch() != null ? r.getFromBranch().getName() : null)
                .toBranch(r.getToBranch() != null ? r.getToBranch().getName() : null)
                .reason(r.getTransferReason())
                .build();
    }

    private DeputationDTO mapDeputation(DeputationRecord r) {
        String status = r.getEndDate() != null && r.getEndDate().isBefore(LocalDate.now())
                ? "Completed" : "Ongoing";
        return DeputationDTO.builder()
                .organization(r.getDeputationOrganization())
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .status(status)
                .build();
    }

    private PayRevisionDTO mapPayRevision(PayRevisionRecord r) {
        return PayRevisionDTO.builder()
                .effectiveDate(r.getEffectiveDate())
                .oldBasic(r.getPreviousPayScaleMin())
                .newBasic(r.getRevisedPayScaleMin())
                .orderNo(r.getPayRevisionOrderNumber())
                .build();
    }

    private TrainingDTO mapTraining(TrainingRecord r) {
        return TrainingDTO.builder()
                .trainingName(r.getTrainingName())
                .provider(r.getProvider())
                .type("Technical")
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .certificate(r.getDocumentName())
                .build();
    }

    private AwardDTO mapAward(AwardRecord r) {
        return AwardDTO.builder()
                .awardName(r.getAwardName())
                .date(r.getAwardDate())
                .issuedBy(getIssuerName(r.getIssuedBy()))
                .build();
    }

    private DisciplinaryDTO mapDisciplinary(DisciplinaryRecord r) {
        return DisciplinaryDTO.builder()
                .caseNo(r.getCaseNumber())
                .action(r.getActionType() != null ? r.getActionType().getName() : null)
                .status(r.getResolutionDate() != null ? "Closed" : "Pending")
                .build();
    }

    private QualificationDTO mapQualification(EmployeeQualification q) {
        return QualificationDTO.builder()
                .qualification(q.getDegree())
                .university(q.getInstitution())
                .year(q.getYear() != null ? q.getYear().toString() : null)
                .build();
    }

    private CertificationDTO mapCertification(EmployeeCertification c) {
        return CertificationDTO.builder()
                .certificate(c.getCertificateName())
                .issuedBy(c.getIssueAuthority())
                .validTill(c.getExpiryDate() != null ? c.getExpiryDate().toString() : null)
                .build();
    }

    // ── Safe Issuer Name Extractor (Replaced Reflection) ──
    private String getIssuerName(Object issuer) {
        if (issuer == null) return null;
        try {
            // Try direct getFullName() (if issuer is an Employee)
            return (String) issuer.getClass().getMethod("getFullName").invoke(issuer);
        } catch (Exception ignored) {
            try {
                // Fallback: Try issuer.getEmployee().getFullName() (if issuer is a Designation)
                Object employee = issuer.getClass().getMethod("getEmployee").invoke(issuer);
                if (employee != null) {
                    return (String) employee.getClass().getMethod("getFullName").invoke(employee);
                }
            } catch (Exception ignored2) {}
        }
        return null;
    }

    // ── Combined Timeline ──────────────────────────────────
    private List<TimelineEventDTO> buildTimeline(
            List<AppointmentRecord> appointments, List<ConfirmationRecord> confirmations,
            List<PromotionRecord> promotions, List<TransferRecord> transfers,
            List<DeputationRecord> deputations, List<PayRevisionRecord> payRevisions,
            List<DisciplinaryRecord> disciplinaries, List<AwardRecord> awards,
            List<TrainingRecord> trainings) {

        List<TimelineEventDTO> events = new ArrayList<>();

        appointments.forEach(r -> events.add(TimelineEventDTO.builder()
                .id(r.getId()).type("appointment")
                .title("Appointment - " + safe(r.getAppointmentOrderNumber()))
                .date(r.getAppointmentDate()).referenceNo(r.getAppointmentOrderNumber())
                .sourceModule("HRMS").remarks(r.getRemarks()).build()));

        confirmations.forEach(r -> events.add(TimelineEventDTO.builder()
                .id(r.getId()).type("confirmation")
                .title("Confirmation - " + safe(r.getConfirmationOrderNumber()))
                .date(r.getConfirmationDate()).referenceNo(r.getConfirmationOrderNumber())
                .sourceModule("HRMS").remarks(r.getRemarks()).build()));

        promotions.forEach(r -> events.add(TimelineEventDTO.builder()
                .id(r.getId()).type("promotion")
                .title("Promotion - " + safe(r.getPromotionOrderNumber()))
                .date(r.getEffectiveDate() != null ? r.getEffectiveDate() : r.getPromotionDate())
                .referenceNo(r.getPromotionOrderNumber())
                .sourceModule("HRMS").remarks(r.getRemarks()).build()));

        transfers.forEach(r -> events.add(TimelineEventDTO.builder()
                .id(r.getId()).type("transfer")
                .title("Transfer - " + safe(r.getTransferOrderNumber()))
                .date(r.getTransferDate()).referenceNo(r.getTransferOrderNumber())
                .sourceModule("HRMS").remarks(r.getTransferReason()).build()));

        deputations.forEach(r -> events.add(TimelineEventDTO.builder()
                .id(r.getId()).type("deputation")
                .title("Deputation - " + safe(r.getDeputationOrganization()))
                .date(r.getStartDate()).referenceNo(r.getDeputationOrderNumber())
                .sourceModule("HRMS").remarks(r.getRemarks()).build()));

        payRevisions.forEach(r -> events.add(TimelineEventDTO.builder()
                .id(r.getId()).type("payRevision")
                .title("Pay Revision - " + safe(r.getPayRevisionOrderNumber()))
                .date(r.getEffectiveDate()).referenceNo(r.getPayRevisionOrderNumber())
                .sourceModule("Payroll").remarks(r.getRemarks()).build()));

        disciplinaries.forEach(r -> events.add(TimelineEventDTO.builder()
                .id(r.getId()).type("disciplinary")
                .title("Disciplinary - " + safe(r.getCaseNumber()))
                .date(r.getIncidentDate()).referenceNo(r.getCaseNumber())
                .sourceModule("HRMS").remarks(r.getRemarks()).build()));

        awards.forEach(r -> events.add(TimelineEventDTO.builder()
                .id(r.getId()).type("award")
                .title(safe(r.getAwardName()))
                .date(r.getAwardDate()).referenceNo("AWD-" + r.getId())
                .sourceModule("HRMS").remarks(r.getDescription()).build()));

        trainings.forEach(r -> events.add(TimelineEventDTO.builder()
                .id(r.getId()).type("training")
                .title(safe(r.getTrainingName()))
                .date(r.getStartDate()).referenceNo("TRN-" + r.getId())
                .sourceModule("Training").build()));

        events.sort(Comparator.comparing(TimelineEventDTO::getDate,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return events;
    }

    private String safe(Object v) {
        return v == null ? "" : v.toString();
    }
}