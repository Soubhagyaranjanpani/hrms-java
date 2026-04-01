package com.hrms.leave.api;

import com.hrms.leave.application.HolidayBulkUploadService;
import com.hrms.leave.application.HolidayUseCase;
import com.hrms.leave.dto.HolidayRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayUseCase holidayUseCase;
    private final HolidayBulkUploadService holidayBulkUploadService;

    @PostMapping
    public Object create(@RequestBody HolidayRequest request) {
        return holidayUseCase.create(request);
    }

    @GetMapping
    public Object getAll() {
        return holidayUseCase.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        holidayUseCase.delete(id);
    }
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        return holidayBulkUploadService.upload(file);
    }
}
