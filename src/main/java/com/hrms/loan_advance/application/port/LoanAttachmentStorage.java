package com.hrms.loan_advance.application.port;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Where loan documents are kept. Today a folder on disk
 * ({@link com.hrms.loan_advance.infrastructure.LoanAttachmentLocalStorage}); later it can be S3 or a
 * file service without touching LoanAdvanceService.
 */
public interface LoanAttachmentStorage {

    /** Saves the file and returns the key to store in loan.attachment. */
    String store(String originalFileName, InputStream content) throws IOException;

    /** The stored file, empty if the key is unknown. */
    Optional<Resource> load(String storedKey);

    boolean exists(String storedKey);
}