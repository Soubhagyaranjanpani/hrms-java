package com.hrms.Awards_Recognition.application;


import com.hrms.Awards_Recognition.domain.AwardRecord;
import com.hrms.Awards_Recognition.infrastructure.AwardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadAwardDocumentUseCase {

    private final AwardRepository awardRepo;
    private final AwardDocumentStorageService storageService;

    public void execute(Long id, MultipartFile file) throws Exception {
        AwardRecord r = awardRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        String path = storageService.saveUploaded(id, file);

        r.setDocumentPath(path);
        r.setDocumentName(file.getOriginalFilename());
        awardRepo.save(r);
    }
}