package com.hrms.leave.job;

import com.hrms.leave.domain.LeaveBalance;
import com.hrms.leave.domain.LeavePolicy;
import com.hrms.leave.infrastructure.LeaveBalanceRepository;
import com.hrms.leave.infrastructure.LeavePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaveAccrualJob {

    private final LeaveBalanceRepository balanceRepo;
    private final LeavePolicyRepository policyRepo;

    @Scheduled(cron = "0 0 1 1 * ?")
    public void accrueLeaves() {

        List<LeavePolicy> policies = policyRepo.findAll();

        for (LeavePolicy policy : policies) {

            if (Boolean.FALSE.equals(policy.getAccrualEnabled())) continue;

            List<LeaveBalance> balances =
                    balanceRepo.findByLeaveType(policy.getLeaveType());

            for (LeaveBalance b : balances) {
                b.setTotalAllocated(b.getTotalAllocated() + policy.getAccrualPerMonth());
                b.setRemaining(b.getRemaining() + policy.getAccrualPerMonth());
                balanceRepo.save(b);
            }
        }
    }
}
