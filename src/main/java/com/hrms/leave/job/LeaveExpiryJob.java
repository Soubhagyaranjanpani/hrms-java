package com.hrms.leave.job;

import com.hrms.leave.domain.LeaveBalance;
import com.hrms.leave.infrastructure.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaveExpiryJob {

    private final LeaveBalanceRepository balanceRepo;

    @Scheduled(cron = "0 0 2 31 12 ?")
    public void expireLeaves() {

        List<LeaveBalance> balances = balanceRepo.findAll();

        for (LeaveBalance b : balances) {

            if (b.getExpiryDate() != null &&
                    b.getExpiryDate().isBefore(LocalDate.now())) {

                b.setRemaining(0.0);
                balanceRepo.save(b);
            }
        }
    }
}
