package com.hrms.attendance.scheduler;

import com.hrms.attendance.application.CloseAttendanceDayUseCase;
import com.hrms.attendance.application.MarkAbsentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttendanceScheduler {

    private final MarkAbsentUseCase markAbsentUseCase;
    private final CloseAttendanceDayUseCase closeAttendanceDayUseCase;

    @Scheduled(cron = "${attendance.absent.cron}")
    public void markAbsentJob() {
        log.info("Running markAbsentJob...");
        markAbsentUseCase.execute();
    }

    @Scheduled(cron = "${attendance.close.cron}")
    public void closeDayJob() {
        log.info("Running closeDayJob...");
        closeAttendanceDayUseCase.execute();
    }
}
