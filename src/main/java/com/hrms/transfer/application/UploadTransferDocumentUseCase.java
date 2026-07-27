// File: com/hrms/transfer/application/UploadTransferDocumentUseCase.java
package com.hrms.transfer.application;

import com.hrms.transfer.domain.TransferRecord;
import com.hrms.transfer.infrastructure.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadTransferDocumentUseCase {

    private final TransferRepository transferRepo;
    private final TransferDocumentStorageService storageService;

    public void execute(Long id, MultipartFile file) throws Exception {
        TransferRecord r = transferRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        String path = storageService.saveUploaded(id, file);

        r.setDocumentPath(path);
        r.setDocumentName(file.getOriginalFilename());
        transferRepo.save(r);
    }
}
