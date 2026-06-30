package com.hrms.employee.application;

import com.hrms.appointment_type.domain.AppointmentAuthority;
import com.hrms.appointment_type.infrastructure.AppointmentAuthorityRepository;
import com.hrms.employee.domain.EmployeeConfirmation;
import com.hrms.employee.dto.EmployeeConfirmationRequest;
import com.hrms.employee.dto.EmployeeConfirmationResponse;
import com.hrms.employee.infrastructure.EmployeeConfirmationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeConfirmationService {

    private final EmployeeConfirmationRepository confirmationRepository;
    private final AppointmentAuthorityRepository authorityRepository;

    public EmployeeConfirmationService(
            EmployeeConfirmationRepository confirmationRepository,
            AppointmentAuthorityRepository authorityRepository) {

        this.confirmationRepository = confirmationRepository;
        this.authorityRepository = authorityRepository;
    }

    // ========================= CREATE =========================
    public EmployeeConfirmationResponse save(EmployeeConfirmationRequest request) {

        AppointmentAuthority authority = authorityRepository.findById(request.getConfirmedById())
                .orElseThrow(() -> new RuntimeException("Appointment Authority not found"));

        EmployeeConfirmation confirmation = EmployeeConfirmation.builder()
                .employeeId(request.getEmployeeId())
                .confirmationOrderNumber(request.getConfirmationOrderNumber())
                .confirmationDate(request.getConfirmationDate())
                .confirmedBy(authority)
                .remarks(request.getRemarks())
                .isDeleted(false)
                .build();

        EmployeeConfirmation saved = confirmationRepository.save(confirmation);

        return mapToResponse(saved);
    }

    // ========================= GET ALL =========================
    public List<EmployeeConfirmationResponse> getAllByFlag(int flag) {

        List<EmployeeConfirmation> confirmations;

        if (flag == 0) {
            confirmations = confirmationRepository.findByIsDeletedFalse();
        } else {
            confirmations = confirmationRepository.findByIsDeletedTrue();
        }

        return confirmations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ========================= GET BY ID =========================
    public EmployeeConfirmationResponse getById(Long id) {

        EmployeeConfirmation confirmation = confirmationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee Confirmation not found"));

        return mapToResponse(confirmation);
    }

    // ========================= UPDATE =========================
    public EmployeeConfirmationResponse update(Long id,
                                               EmployeeConfirmationRequest request) {

        EmployeeConfirmation confirmation = confirmationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee Confirmation not found"));

        AppointmentAuthority authority = authorityRepository.findById(request.getConfirmedById())
                .orElseThrow(() -> new RuntimeException("Appointment Authority not found"));

        confirmation.setEmployeeId(request.getEmployeeId());
        confirmation.setConfirmationOrderNumber(request.getConfirmationOrderNumber());
        confirmation.setConfirmationDate(request.getConfirmationDate());
        confirmation.setConfirmedBy(authority);
        confirmation.setRemarks(request.getRemarks());

        EmployeeConfirmation updated = confirmationRepository.save(confirmation);

        return mapToResponse(updated);
    }

    // ========================= SOFT DELETE =========================
    public void delete(Long id) {

        EmployeeConfirmation confirmation = confirmationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee Confirmation not found"));

        confirmation.setIsDeleted(true);

        confirmationRepository.save(confirmation);
    }

    // ========================= RESPONSE MAPPER =========================
    private EmployeeConfirmationResponse mapToResponse(EmployeeConfirmation confirmation) {

        return EmployeeConfirmationResponse.builder()
                .id(confirmation.getId())
                .employeeId(confirmation.getEmployeeId())
                .confirmationOrderNumber(confirmation.getConfirmationOrderNumber())
                .confirmationDate(confirmation.getConfirmationDate())
                .confirmedById(
                        confirmation.getConfirmedBy() != null
                                ? confirmation.getConfirmedBy().getId()
                                : null
                )
                .confirmedByName(
                        confirmation.getConfirmedBy() != null
                                ? confirmation.getConfirmedBy().getAuthorityName()
                                : null
                )
                .remarks(confirmation.getRemarks())
                .document(confirmation.getDocument())
                .build();
    }
}