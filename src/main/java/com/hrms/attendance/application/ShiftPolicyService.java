package com.hrms.attendance.application;

import com.hrms.attendance.domain.AttendancePolicy;
import com.hrms.attendance.domain.ShiftMaster;
import com.hrms.attendance.domain.ShiftType;
import com.hrms.attendance.dto.ShiftPolicySettingsDTO;
import com.hrms.attendance.dto.ShiftResponse;
import com.hrms.attendance.infrastructure.AttendancePolicyRepository;
import com.hrms.attendance.infrastructure.ShiftMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShiftPolicyService {

    private final AttendancePolicyRepository policyRepo;
    private final ShiftMasterRepository shiftRepo;

    // In-memory policy settings cache (default fallback)
    private static volatile int checkInBeforeMinutes = 30;
    private static volatile boolean enforceCheckOutAfterShift = true;
    private static volatile int checkOutGraceMinutes = 10;

    public List<ShiftResponse> getAllShifts() {
        try {
            List<ShiftMaster> list = shiftRepo.findByIsActiveTrueOrderByIdAsc();
            if (!list.isEmpty()) {
                return list.stream().map(this::toResponse).collect(Collectors.toList());
            }
        } catch (Exception ex) {
            log.warn("Shift master DB read fallback: {}", ex.getMessage());
        }

        return Arrays.stream(ShiftType.values())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ShiftResponse getShiftByCode(String code) {
        if (code != null) {
            try {
                Optional<ShiftMaster> opt = shiftRepo.findByCodeIgnoreCase(code.trim());
                if (opt.isPresent()) {
                    return toResponse(opt.get());
                }
            } catch (Exception ex) {
                log.warn("Shift master by code DB read fallback: {}", ex.getMessage());
            }
        }

        ShiftType type = ShiftType.fromCode(code);
        return toResponse(type);
    }

    @Transactional
    public ShiftResponse saveShift(ShiftMaster req) {
        if (req.getCode() == null || req.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Shift code is required");
        }
        String code = req.getCode().trim().toUpperCase();
        req.setCode(code);

        calculateDuration(req);

        // Check if existing by code
        Optional<ShiftMaster> existingOpt = shiftRepo.findByCodeIgnoreCase(code);
        ShiftMaster target = existingOpt.orElse(req);

        if (existingOpt.isPresent()) {
            applyShiftUpdates(target, req);
        }

        ShiftMaster saved = shiftRepo.save(target);
        log.info("Saved shift {} (id={}) to PostgreSQL shift_master", saved.getCode(), saved.getId());

        // Synchronize with active attendance policy if GS or active
        syncWithActivePolicyIfRelevant(saved);

        return toResponse(saved);
    }

    @Transactional
    public ShiftResponse updateShift(Long id, ShiftMaster req) {
        ShiftMaster existing = shiftRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + id));

        applyShiftUpdates(existing, req);
        calculateDuration(existing);

        ShiftMaster saved = shiftRepo.save(existing);
        log.info("Updated shift {} (id={}) in PostgreSQL shift_master", saved.getCode(), saved.getId());

        syncWithActivePolicyIfRelevant(saved);

        return toResponse(saved);
    }

    @Transactional
    public void deleteShift(Long id) {
        ShiftMaster existing = shiftRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + id));
        existing.setIsActive(false);
        shiftRepo.save(existing);
        log.info("Soft deleted shift {} (id={})", existing.getCode(), id);
    }

    private void calculateDuration(ShiftMaster s) {
        if (s.getStartTime() != null && s.getEndTime() != null) {
            long mins = Duration.between(s.getStartTime(), s.getEndTime()).toMinutes();
            boolean isNight = mins < 0 || Boolean.TRUE.equals(s.getIsNightShift());
            if (mins <= 0 || isNight) {
                mins += 24 * 60;
                s.setIsNightShift(true);
            }
            s.setDurationHours(Math.round((mins / 60.0) * 10.0) / 10.0);
        }
    }

    private void applyShiftUpdates(ShiftMaster target, ShiftMaster req) {
        if (req.getName() != null) target.setName(req.getName());
        if (req.getShortCode() != null) target.setShortCode(req.getShortCode());
        if (req.getStartTime() != null) target.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) target.setEndTime(req.getEndTime());
        if (req.getDurationHours() != null) target.setDurationHours(req.getDurationHours());
        if (req.getBreakMinutes() != null) target.setBreakMinutes(req.getBreakMinutes());
        if (req.getGraceMinutes() != null) target.setGraceMinutes(req.getGraceMinutes());
        if (req.getIsNightShift() != null) target.setIsNightShift(req.getIsNightShift());
        if (req.getColor() != null) target.setColor(req.getColor());
        if (req.getBgColor() != null) target.setBgColor(req.getBgColor());
        if (req.getBorderColor() != null) target.setBorderColor(req.getBorderColor());
        if (req.getIsActive() != null) target.setIsActive(req.getIsActive());
        if (req.getDescription() != null) target.setDescription(req.getDescription());
    }

    private void syncWithActivePolicyIfRelevant(ShiftMaster shift) {
        try {
            if ("GS".equalsIgnoreCase(shift.getCode()) || "12-9".equalsIgnoreCase(shift.getCode())) {
                policyRepo.findFirstByIsActiveTrueOrderByIdDesc().ifPresent(p -> {
                    if (shift.getStartTime() != null) p.setShiftStart(shift.getStartTime());
                    if (shift.getEndTime() != null) p.setShiftEnd(shift.getEndTime());
                    if (shift.getGraceMinutes() != null) p.setGraceMinutes(shift.getGraceMinutes());
                    if (shift.getBreakMinutes() != null) p.setBreakMinutes(shift.getBreakMinutes());
                    if (shift.getIsNightShift() != null) p.setIsNightShift(shift.getIsNightShift());
                    policyRepo.save(p);
                    log.info("Synchronized active attendance policy timings from shift {}", shift.getCode());
                });
            }
        } catch (Exception ex) {
            log.warn("Active policy sync note: {}", ex.getMessage());
        }
    }

    public ShiftPolicySettingsDTO getShiftSettings() {
        int inMins = checkInBeforeMinutes;
        boolean enforce = enforceCheckOutAfterShift;
        int grace = checkOutGraceMinutes;

        try {
            var optPolicy = policyRepo.findFirstByIsActiveTrueOrderByIdDesc();
            if (optPolicy.isPresent()) {
                AttendancePolicy p = optPolicy.get();
                if (p.getCheckInBeforeMinutes() != null) inMins = p.getCheckInBeforeMinutes();
                if (p.getEnforceCheckOutAfterShift() != null) enforce = p.getEnforceCheckOutAfterShift();
                if (p.getCheckOutGraceMinutes() != null) grace = p.getCheckOutGraceMinutes();
                else if (p.getGraceMinutes() != null) grace = p.getGraceMinutes();
            }
        } catch (Exception ex) {
            log.warn("Reading shift settings fallback: {}", ex.getMessage());
        }

        return ShiftPolicySettingsDTO.builder()
                .checkInBeforeMinutes(inMins)
                .enforceCheckOutAfterShift(enforce)
                .checkOutGraceMinutes(grace)
                .availableShifts(getAllShifts())
                .build();
    }

    public ShiftPolicySettingsDTO updateShiftSettings(ShiftPolicySettingsDTO dto) {
        if (dto.getCheckInBeforeMinutes() != null) {
            checkInBeforeMinutes = Math.max(0, dto.getCheckInBeforeMinutes());
        }
        if (dto.getEnforceCheckOutAfterShift() != null) {
            enforceCheckOutAfterShift = dto.getEnforceCheckOutAfterShift();
        }
        if (dto.getCheckOutGraceMinutes() != null) {
            checkOutGraceMinutes = Math.max(0, dto.getCheckOutGraceMinutes());
        }

        // Persist all settings to active PostgreSQL attendance_policy row
        try {
            policyRepo.findFirstByIsActiveTrueOrderByIdDesc().ifPresent(policy -> {
                if (dto.getCheckInBeforeMinutes() != null) {
                    policy.setCheckInBeforeMinutes(dto.getCheckInBeforeMinutes());
                }
                if (dto.getEnforceCheckOutAfterShift() != null) {
                    policy.setEnforceCheckOutAfterShift(dto.getEnforceCheckOutAfterShift());
                }
                if (dto.getCheckOutGraceMinutes() != null) {
                    policy.setCheckOutGraceMinutes(dto.getCheckOutGraceMinutes());
                    policy.setGraceMinutes(dto.getCheckOutGraceMinutes());
                }
                policyRepo.save(policy);
                log.info("Synchronized shift settings to PostgreSQL attendance_policy ID {}", policy.getId());
            });
        } catch (Exception ex) {
            log.warn("Database sync note for attendance policy settings: {}", ex.getMessage());
        }

        return getShiftSettings();
    }

    public ShiftResponse getMyShift(String email, String shiftCode) {
        // If an explicit shiftCode is provided (e.g. from employee schedule or active roster)
        if (shiftCode != null && !shiftCode.trim().isEmpty()) {
            return toResponse(ShiftType.fromCode(shiftCode));
        }

        // If active policy specifies a shiftCode, check that
        try {
            var optPolicy = policyRepo.findFirstByIsActiveTrueOrderByIdDesc();
            if (optPolicy.isPresent() && optPolicy.get().getShiftCode() != null) {
                return toResponse(ShiftType.fromCode(optPolicy.get().getShiftCode()));
            }
        } catch (Exception ignored) {}

        // Default to General Shift (09:00 - 18:00)
        return toResponse(ShiftType.GS);
    }

    public int getCheckInBeforeMinutes() {
        try {
            var p = policyRepo.findFirstByIsActiveTrueOrderByIdDesc();
            if (p.isPresent() && p.get().getCheckInBeforeMinutes() != null) {
                return p.get().getCheckInBeforeMinutes();
            }
        } catch (Exception ignored) {}
        return checkInBeforeMinutes;
    }

    public boolean isEnforceCheckOutAfterShift() {
        try {
            var p = policyRepo.findFirstByIsActiveTrueOrderByIdDesc();
            if (p.isPresent() && p.get().getEnforceCheckOutAfterShift() != null) {
                return p.get().getEnforceCheckOutAfterShift();
            }
        } catch (Exception ignored) {}
        return enforceCheckOutAfterShift;
    }

    public int getCheckOutGraceMinutes() {
        try {
            var p = policyRepo.findFirstByIsActiveTrueOrderByIdDesc();
            if (p.isPresent() && p.get().getCheckOutGraceMinutes() != null) {
                return p.get().getCheckOutGraceMinutes();
            }
        } catch (Exception ignored) {}
        return checkOutGraceMinutes;
    }

    public ShiftType resolveShift(String shiftCode, AttendancePolicy fallbackPolicy) {
        if (shiftCode != null && !shiftCode.trim().isEmpty()) {
            return ShiftType.fromCode(shiftCode);
        }
        return ShiftType.GS;
    }

    private ShiftResponse toResponse(ShiftMaster m) {
        return ShiftResponse.builder()
                .id(m.getId())
                .code(m.getCode())
                .shortCode(m.getShortCode() != null ? m.getShortCode() : m.getCode())
                .name(m.getName())
                .startTime(m.getStartTime())
                .endTime(m.getEndTime())
                .durationHours(m.getDurationHours())
                .breakMinutes(m.getBreakMinutes())
                .graceMinutes(m.getGraceMinutes())
                .isNightShift(m.getIsNightShift())
                .color(m.getColor())
                .bgColor(m.getBgColor())
                .borderColor(m.getBorderColor())
                .isActive(m.getIsActive())
                .description(m.getDescription())
                .build();
    }

    private ShiftResponse toResponse(ShiftType type) {
        return ShiftResponse.builder()
                .code(type.getCode())
                .name(type.getName())
                .startTime(type.getShiftStart())
                .endTime(type.getShiftEnd())
                .durationHours(type.getDurationHours())
                .breakMinutes(type.getBreakMinutes())
                .graceMinutes(type.getGraceMinutes())
                .isNightShift(type.isNightShift())
                .isActive(true)
                .description(type.getDescription())
                .build();
    }
}
