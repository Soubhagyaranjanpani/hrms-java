package com.hrms.leave.application;

import com.hrms.leave.domain.Holiday;
import com.hrms.leave.infrastructure.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayBulkUploadService {

    private final HolidayRepository holidayRepo;

    public String upload(MultipartFile file) {
        List<Holiday> holidaysToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Skip header if exists (check if line contains "name" or "date")
                if (lineNumber == 1 && (line.toLowerCase().contains("name") || line.toLowerCase().contains("date"))) {
                    log.info("Skipping header line: {}", line);
                    continue;
                }

                try {
                    // CSV format: name,date
                    String[] parts = line.split(",");

                    if (parts.length < 2) {
                        errors.add(String.format("Line %d: Invalid format (expected 'name,date'): %s", lineNumber, line));
                        continue;
                    }

                    String name = parts[0].trim();
                    String dateStr = parts[1].trim();

                    // Validate name
                    if (name.isEmpty()) {
                        errors.add(String.format("Line %d: Holiday name cannot be empty", lineNumber));
                        continue;
                    }

                    // Parse date
                    LocalDate date;
                    try {
                        date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                    } catch (Exception e) {
                        errors.add(String.format("Line %d: Invalid date format '%s'. Expected YYYY-MM-DD", lineNumber, dateStr));
                        continue;
                    }

                    // Check if holiday already exists for this date (company-wide)
                    boolean exists = holidayRepo.findByDateAndBranchIsNull(date).isPresent();
                    if (exists) {
                        errors.add(String.format("Line %d: Holiday already exists for date %s", lineNumber, date));
                        continue;
                    }

                    // Create holiday exactly like the create method does
                    Holiday h = new Holiday();
                    h.setName(name);
                    h.setDate(date);
                    h.setDescription(name); // Set description as name if not provided
                    h.setIsActive(true);
                    h.setIsDeleted(false);
                    // branch is null for company-wide holidays (same as create method)

                    holidaysToSave.add(h);

                } catch (Exception e) {
                    errors.add(String.format("Line %d: Unexpected error - %s", lineNumber, e.getMessage()));
                    log.error("Error processing line {}: {}", lineNumber, line, e);
                }
            }

            // Save all valid holidays
            if (!holidaysToSave.isEmpty()) {
                holidayRepo.saveAll(holidaysToSave);
                log.info("Successfully saved {} holidays", holidaysToSave.size());
            }

            // Build response message
            StringBuilder result = new StringBuilder();
            result.append(String.format("Upload completed: %d holidays added successfully", holidaysToSave.size()));

            if (!errors.isEmpty()) {
                result.append(String.format(", %d failed", errors.size()));
                result.append("\n\nErrors:\n");
                for (String error : errors) {
                    result.append("• ").append(error).append("\n");
                }
            }

            if (holidaysToSave.isEmpty() && !errors.isEmpty()) {
                throw new RuntimeException(result.toString());
            }

            return result.toString();

        } catch (Exception e) {
            log.error("File processing failed", e);
            throw new RuntimeException("File processing failed: " + e.getMessage());
        }
    }
}