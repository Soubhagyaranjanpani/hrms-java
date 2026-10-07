package com.hrms.disciplinary.application;

import com.hrms.disciplinary.domain.DisciplinaryRecord;
import com.hrms.disciplinary.infrastructure.DisciplinaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadDisciplinaryDocumentUseCase {

    private final DisciplinaryRepository disciplinaryRepo;
    private final DisciplinaryDocumentStorageService storageService;

    public void execute(Long id, MultipartFile file) throws Exception {
        DisciplinaryRecord r = disciplinaryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        String path = storageService.saveUploaded(id, file);

        r.setDocumentPath(path);
        r.setDocumentName(file.getOriginalFilename());
        disciplinaryRepo.save(r);
    }
}