package com.hrms.leave.application;

import com.hrms.leave.domain.Holiday;
import com.hrms.leave.infrastructure.HolidayRepository;
import com.hrms.leave.dto.HolidayRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayUseCase {

    private final HolidayRepository holidayRepo;

    public Holiday create(HolidayRequest request) {

        Holiday h = new Holiday();
        h.setName(request.getName());
        h.setDate(request.getDate());
        h.setDescription(request.getDescription());

        return holidayRepo.save(h);
    }

    public List<Holiday> getAll() {
        return holidayRepo.findAll();
    }

    public void delete(Long id) {
        Holiday h = holidayRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Holiday not found"));
        h.setIsDeleted(true);
        holidayRepo.save(h);
    }
}
