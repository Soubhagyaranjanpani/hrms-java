package com.hrms.leave.api;

import com.hrms.leave.application.*;
import com.hrms.leave.dto.LeaveApplyRequest;
import com.hrms.leave.dto.LeaveApprovalRequest;
import com.zaxxer.hikari.pool.HikariProxyPreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final ApplyLeaveUseCase applyLeaveUseCase;
    private final ApproveLeaveUseCase approveLeaveUseCase;
    private final RejectLeaveUseCase rejectLeaveUseCase;
    private final GetTeamLeavesUseCase getTeamLeavesUseCase;
    private final GetMyLeavesUseCase getMyLeavesUseCase;
    private final GetLeaveCalendarUseCase getLeaveCalendarUseCase;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestBody LeaveApplyRequest request) {
        return ResponseEntity.ok(applyLeaveUseCase.execute(request));
    }
    @PostMapping("/approve")
    public ResponseEntity<?> approve(
            @RequestBody LeaveApprovalRequest request,
            Principal principal) {


        return ResponseEntity.ok(
                approveLeaveUseCase.execute(request, principal.getName())
        );
    }

    @PostMapping("/reject")
    public ResponseEntity<?> reject(
            @RequestBody LeaveApprovalRequest request,
            Principal principal) {

        return ResponseEntity.ok(
                rejectLeaveUseCase.execute(request, principal.getName())
        );
    }
    @GetMapping("/my")
    public ResponseEntity<?> getMyLeaves(Principal principal) {
        return ResponseEntity.ok(getMyLeavesUseCase.execute(principal));
    }

    @GetMapping("/team")
    public ResponseEntity<?> getTeamLeaves(Principal principal) {
        return ResponseEntity.ok(getTeamLeavesUseCase.execute(principal));
    }

    @GetMapping("/calendar")
    public ResponseEntity<?> getCalendar(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        return ResponseEntity.ok(
                getLeaveCalendarUseCase.execute(
                        LocalDate.parse(startDate),
                        LocalDate.parse(endDate)
                )
        );
    }
}
