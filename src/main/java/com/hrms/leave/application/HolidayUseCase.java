package com.hrms.leave.application;

import com.hrms.leave.domain.Holiday;
import com.hrms.leave.dto.HolidayRequest;
import com.hrms.leave.dto.HolidayResponse;
import com.hrms.leave.infrastructure.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HolidayUseCase {

    private final HolidayRepository holidayRepo;

    // 🔥 CREATE
    public HolidayResponse create(HolidayRequest request) {

        Holiday h = new Holiday();
        h.setName(request.getName());
        h.setDate(request.getDate());
        h.setDescription(request.getDescription());
        h.setIsActive(true);
        h.setIsDeleted(false);

        Holiday saved = holidayRepo.save(h);

        return mapToResponse(saved);
    }

    // 🔥 GET ALL (exclude deleted)
    public List<HolidayResponse> getAll() {

        return holidayRepo.findAll().stream()
                .filter(h -> !Boolean.TRUE.equals(h.getIsDeleted()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔥 DELETE (soft delete)
    public void delete(Long id) {

        Holiday h = holidayRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Holiday not found"));

        h.setIsDeleted(true);
        holidayRepo.save(h);
    }

    // 🔥 MAPPER
    private HolidayResponse mapToResponse(Holiday h) {

        HolidayResponse res = new HolidayResponse();
        res.setId(h.getId());
        res.setName(h.getName());
        res.setDate(h.getDate());
        res.setIsActive(h.getIsActive());

        return res;
    }
}
