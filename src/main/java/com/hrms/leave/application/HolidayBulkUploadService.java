package com.hrms.leave.application;

import com.hrms.leave.domain.Holiday;
import com.hrms.leave.infrastructure.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HolidayBulkUploadService {

    private final HolidayRepository holidayRepo;

    public String upload(MultipartFile file) {

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {

                // CSV format: name,date
                String[] parts = line.split(",");

                Holiday h = new Holiday();
                h.setName(parts[0]);
                h.setDate(LocalDate.parse(parts[1]));

                holidayRepo.save(h);
            }

            return "Upload successful";

        } catch (Exception e) {
            throw new RuntimeException("File processing failed");
        }
    }
}
