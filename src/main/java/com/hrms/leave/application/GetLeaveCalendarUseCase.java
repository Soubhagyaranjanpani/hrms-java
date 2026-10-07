package com.hrms.leave.application;

import com.hrms.leave.domain.Leave;
import com.hrms.leave.dto.LeaveCalendarResponse;
import com.hrms.leave.infrastructure.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GetLeaveCalendarUseCase {

    private final LeaveRepository leaveRepo;

    public List<LeaveCalendarResponse> execute(LocalDate start, LocalDate end) {

        List<Leave> leaves = leaveRepo.findByStartDateBetweenAndIsDeletedFalse(start, end);

        Map<LocalDate, List<String>> map = new HashMap<>();

        for (Leave leave : leaves) {

            for (LocalDate d = leave.getStartDate();
                 !d.isAfter(leave.getEndDate());
                 d = d.plusDays(1)) {

                map.computeIfAbsent(d, k -> new ArrayList<>())
                        .add(leave.getEmployee().getFirstName());
            }
        }

        List<LeaveCalendarResponse> result = new ArrayList<>();

        map.forEach((date, employees) -> {
            LeaveCalendarResponse res = new LeaveCalendarResponse();
            res.setDate(date);
            res.setEmployeesOnLeave(employees);
            result.add(res);
        });

        return result;
    }
}
