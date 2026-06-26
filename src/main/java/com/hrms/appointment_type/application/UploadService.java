package com.hrms.appointment_type.application;

import com.hrms.appointment_type.domain.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UploadService {

    String uploadFile(String name, MultipartFile image) throws Exception;

    List<UploadFile> getAllFiles();

    UploadFile getFileById(Long id);

    UploadFile updateFile(Long id, UploadFile file);

    byte[] downloadFile(Long id) throws Exception;
}