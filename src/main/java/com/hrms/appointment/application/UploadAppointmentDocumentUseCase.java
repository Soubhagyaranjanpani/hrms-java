// File: com/hrms/appointment/application/UploadAppointmentDocumentUseCase.java
package com.hrms.appointment.application;

import com.hrms.appointment.domain.AppointmentRecord;
import com.hrms.appointment.infrastructure.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class UploadAppointmentDocumentUseCase {

    private final AppointmentRepository appointmentRepo;
    private final AppointmentDocumentStorageService storageService;

    public void execute(Long id, MultipartFile file) throws Exception {
        AppointmentRecord r = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        String path = storageService.saveUploaded(id, file);

        r.setDocumentPath(path);
        r.setDocumentName(file.getOriginalFilename());
        appointmentRepo.save(r);
    }
}
