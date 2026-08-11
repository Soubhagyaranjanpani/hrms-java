// path: src/main/java/com/hrms/Service_Book_Document/application/ServiceBookDocumentService.java
package com.hrms.Service_Book_Document.application;

import com.hrms.Awards_Recognition.infrastructure.AwardRepository;
import com.hrms.Confirmation.infrastructure.ConfirmationRepository;
import com.hrms.Service_Book_Document.dto.DocumentRecordDTO;

import com.hrms.Awards_Recognition.domain.AwardRecord;
import com.hrms.Confirmation.domain.ConfirmationRecord;
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
public class ServiceBookDocumentService {

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

    public List<DocumentRecordDTO> getDocumentsForEmployee(Long employeeId) {
        List<DocumentRecordDTO> docs = new ArrayList<>();

        appointmentRepo.findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(employeeId)
                .forEach(r -> docs.add(mapAppointment(r)));

        confirmationRepo.findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(employeeId)
                .forEach(r -> docs.add(mapConfirmation(r)));

        promotionRepo.findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(employeeId)
                .forEach(r -> docs.add(mapPromotion(r)));

        transferRepo.findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(employeeId)
                .forEach(r -> docs.add(mapTransfer(r)));

        deputationRepo.findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(employeeId)
                .forEach(r -> docs.add(mapDeputation(r)));

        payRevisionRepo.findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(employeeId)
                .forEach(r -> docs.add(mapPayRevision(r)));

        disciplinaryRepo.findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(employeeId)
                .forEach(r -> docs.add(mapDisciplinary(r)));

        awardRepo.findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(employeeId)
                .forEach(r -> docs.add(mapAward(r)));

        trainingRepo.findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(employeeId)
                .forEach(r -> docs.add(mapTraining(r)));

        retirementRepo.findByEmployee_IdAndIsDeletedFalseAndDocumentPathIsNotNull(employeeId)
                .forEach(r -> docs.add(mapRetirement(r)));

        docs.sort(Comparator.comparing(
                DocumentRecordDTO::getEventDate,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        return docs;
    }

    private DocumentRecordDTO mapAppointment(AppointmentRecord r) {
        return DocumentRecordDTO.builder()
                .recordId(r.getId())
                .category("appointment")
                .categoryLabel("Appointment Order")
                .title("Appointment Order - " + safe(r.getAppointmentOrderNumber()))
                .referenceNo(r.getAppointmentOrderNumber())
                .eventDate(r.getAppointmentDate())
                .documentName(r.getDocumentName())
                .documentPath(r.getDocumentPath())
                .fileType(extension(r.getDocumentName()))
                .uploadedAt(r.getCreatedAt())
                .build();
    }

    private DocumentRecordDTO mapConfirmation(ConfirmationRecord r) {
        return DocumentRecordDTO.builder()
                .recordId(r.getId())
                .category("confirmation")
                .categoryLabel("Confirmation Order")
                .title("Confirmation Order - " + safe(r.getConfirmationOrderNumber()))
                .referenceNo(r.getConfirmationOrderNumber())
                .eventDate(r.getConfirmationDate())
                .documentName(r.getDocumentName())
                .documentPath(r.getDocumentPath())
                .fileType(extension(r.getDocumentName()))
                .uploadedAt(r.getCreatedAt())
                .build();
    }

    private DocumentRecordDTO mapPromotion(PromotionRecord r) {
        return DocumentRecordDTO.builder()
                .recordId(r.getId())
                .category("promotion")
                .categoryLabel("Promotion Order")
                .title("Promotion Order - " + safe(r.getPromotionOrderNumber()))
                .referenceNo(r.getPromotionOrderNumber())
                .eventDate(r.getPromotionDate() != null ? r.getPromotionDate() : r.getEffectiveDate())
                .documentName(r.getDocumentName())
                .documentPath(r.getDocumentPath())
                .fileType(extension(r.getDocumentName()))
                .uploadedAt(r.getCreatedAt())
                .build();
    }

    private DocumentRecordDTO mapTransfer(TransferRecord r) {
        return DocumentRecordDTO.builder()
                .recordId(r.getId())
                .category("transfer")
                .categoryLabel("Transfer Order")
                .title("Transfer Order - " + safe(r.getTransferOrderNumber()))
                .referenceNo(r.getTransferOrderNumber())
                .eventDate(r.getTransferDate())
                .documentName(r.getDocumentName())
                .documentPath(r.getDocumentPath())
                .fileType(extension(r.getDocumentName()))
                .uploadedAt(r.getCreatedAt())
                .build();
    }

    private DocumentRecordDTO mapDeputation(DeputationRecord r) {
        return DocumentRecordDTO.builder()
                .recordId(r.getId())
                .category("deputation")
                .categoryLabel("Deputation Order")
                .title("Deputation Order - " + safe(r.getDeputationOrderNumber()))
                .referenceNo(r.getDeputationOrderNumber())
                .eventDate(r.getStartDate())
                .documentName(r.getDocumentName())
                .documentPath(r.getDocumentPath())
                .fileType(extension(r.getDocumentName()))
                .uploadedAt(r.getCreatedAt())
                .build();
    }

    private DocumentRecordDTO mapPayRevision(PayRevisionRecord r) {
        return DocumentRecordDTO.builder()
                .recordId(r.getId())
                .category("payRevision")
                .categoryLabel("Pay Revision Order")
                .title("Pay Revision Order - " + safe(r.getPayRevisionOrderNumber()))
                .referenceNo(r.getPayRevisionOrderNumber())
                .eventDate(r.getEffectiveDate())
                .documentName(r.getDocumentName())
                .documentPath(r.getDocumentPath())
                .fileType(extension(r.getDocumentName()))
                .uploadedAt(r.getCreatedAt())
                .build();
    }

    private DocumentRecordDTO mapDisciplinary(DisciplinaryRecord r) {
        return DocumentRecordDTO.builder()
                .recordId(r.getId())
                .category("disciplinary")
                .categoryLabel("Disciplinary Case File")
                .title("Case File - " + safe(r.getCaseNumber()))
                .referenceNo(r.getCaseNumber())
                .eventDate(r.getIncidentDate())
                .documentName(r.getDocumentName())
                .documentPath(r.getDocumentPath())
                .fileType(extension(r.getDocumentName()))
                .uploadedAt(r.getCreatedAt())
                .build();
    }

    private DocumentRecordDTO mapAward(AwardRecord r) {
        return DocumentRecordDTO.builder()
                .recordId(r.getId())
                .category("award")
                .categoryLabel("Award Certificate")
                .title(safe(r.getAwardName()))
                .referenceNo("AWD-" + r.getId())
                .eventDate(r.getAwardDate())
                .documentName(r.getDocumentName())
                .documentPath(r.getDocumentPath())
                .fileType(extension(r.getDocumentName()))
                .uploadedAt(r.getCreatedAt())
                .build();
    }

    private DocumentRecordDTO mapTraining(TrainingRecord r) {
        return DocumentRecordDTO.builder()
                .recordId(r.getId())
                .category("training")
                .categoryLabel("Training Certificate")
                .title(safe(r.getTrainingName()))
                .referenceNo("TRN-" + r.getId())
                .eventDate(r.getStartDate())
                .documentName(r.getDocumentName())
                .documentPath(r.getDocumentPath())
                .fileType(extension(r.getDocumentName()))
                .uploadedAt(r.getCreatedAt())
                .build();
    }

    private DocumentRecordDTO mapRetirement(RetirementRecord r) {
        return DocumentRecordDTO.builder()
                .recordId(r.getId())
                .category("retirement")
                .categoryLabel("Retirement Order")
                .title("Retirement Order - " + safe(r.getRetirementOrder()))
                .referenceNo(r.getRetirementOrder())
                .eventDate(r.getRetirementDate())
                .documentName(r.getDocumentName())
                .documentPath(r.getDocumentPath())
                .fileType(extension(r.getDocumentName()))
                .uploadedAt(r.getCreatedAt())
                .build();
    }

    private String safe(Object v) {
        return v == null ? "" : v.toString();
    }

    private String extension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "unknown";
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}