package com.hrms.leave.job;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.leave.domain.LeaveBalance;
import com.hrms.leave.domain.LeavePolicy;
import com.hrms.leave.infrastructure.LeaveBalanceRepository;
import com.hrms.leave.infrastructure.LeavePolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaveAccrualJob {

    private final LeaveBalanceRepository balanceRepo;
    private final LeavePolicyRepository policyRepo;
    private final EmployeeRepository employeeRepo;

    @Scheduled(cron = "${leave.accrual.cron}")
    @Transactional
    public void accrueLeaves() {

        LocalDate today = LocalDate.now();

        log.info("Starting Leave Accrual Job for {}", today);

        // 🔥 Only active policies
        List<LeavePolicy> policies = policyRepo.findByIsActiveTrue();

        for (LeavePolicy policy : policies) {

            if (Boolean.FALSE.equals(policy.getAccrualEnabled())) continue;

            List<Employee> employees = employeeRepo.findAll(); // ideally active only

            for (Employee emp : employees) {

                // 🔥 Fetch or create balance
                LeaveBalance balance = balanceRepo
                        .findByEmployeeAndLeaveType(emp, policy.getLeaveType())
                        .orElseGet(() -> {
                            LeaveBalance b = new LeaveBalance();
                            b.setEmployee(emp);
                            b.setLeaveType(policy.getLeaveType());
                            b.setTotalAllocated((double) 0);
                            b.setRemaining((double) 0);
                            return b;
                        });

                // 🔥 Prevent duplicate accrual (monthly)
                if (balance.getLastAccrualDate() != null &&
                        balance.getLastAccrualDate().getMonth() == today.getMonth() &&
                        balance.getLastAccrualDate().getYear() == today.getYear()) {
                    continue;
                }

                double increment = policy.getAccrualPerMonth();

                int max = policy.getLeaveType().getMaxDaysPerYear();

                double newTotal = balance.getTotalAllocated() + increment;

                // 🔥 Cap at max limit
                if (newTotal > max) {
                    increment = max - balance.getTotalAllocated();
                    if (increment <= 0) continue;
                }

                balance.setTotalAllocated(balance.getTotalAllocated() + increment);
                balance.setRemaining(balance.getRemaining() + increment);

                // 🔥 Track accrual
                balance.setLastAccrualDate(today);

                balanceRepo.save(balance);
            }
        }

        log.info("Leave Accrual Job completed");
    }
}
