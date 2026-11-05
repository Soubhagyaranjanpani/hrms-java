package com.hrms.loan_advance.application;

import com.hrms.loan_advance.application.port.LoanEmployeePort;
import com.hrms.loan_advance.domain.ApprovalChain;
import com.hrms.loan_advance.domain.ApprovalStep;
import com.hrms.loan_advance.domain.LoanAdvance;
import com.hrms.loan_advance.domain.LoanStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Who may see a loan and who may act on it. Only reads the logged-in user and the loan; no database access
 * except the one lookup of the current employee.
 */
@Component
@RequiredArgsConstructor
public class LoanAccessGuard {

    private static final String ADMIN_ROLE = "Admin";

    private final LoanEmployeePort employees;

    /** Role name (roles.name) of the HR Head stage. */
    @Value("${hrms.loan.approval.hr-role:HR}")
    private String hrRole = "HR";

    /** Role name (roles.name) of the Finance stage. */
    @Value("${hrms.loan.approval.finance-role:Finance}")
    private String financeRole = "Finance";

    /** false (default): nobody can approve or reject their own loan. Set true only for local testing with one user. */
    @Value("${hrms.loan.allow-self-approval:false}")
    private boolean allowSelfApproval = false;

    public LoanActor currentActor() {
        return new LoanActor(employees.currentEmployee(), hasRole(ADMIN_ROLE), hasRole(hrRole), hasRole(financeRole));
    }

    public boolean allowsSelfApproval() {
        return allowSelfApproval;
    }

    public boolean isOwner(LoanActor me, String employeeCode) {
        return employeeCode != null && employeeCode.equals(me.employeeCode());
    }

    /** The loan's owner, or HR/Admin. */
    public void assertCanAccess(LoanActor me, String employeeCode) {
        if (!me.hrOrAdmin() && !isOwner(me, employeeCode)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this loan request");
        }
    }

    /** Admin, HR or Finance only (payroll side: record EMIs, foreclose). */
    public void assertPrivileged(LoanActor me) {
        if (!me.privileged()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only HR, Finance or Admin can do this");
        }
    }

    /** Read access: owner, Admin/HR/Finance, or the applicant's reporting manager. */
    public boolean canView(LoanActor me, LoanAdvance loan) {
        if (me.privileged() || isOwner(me, loan.getEmpId())) return true;
        ApprovalChain chain = loan.getApproverChain();
        return chain != null && me.employeeCode().equals(chain.getReportingManagerCode());
    }

    public void assertCanView(LoanActor me, LoanAdvance loan) {
        if (!canView(me, loan)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this loan request");
        }
    }

    /** true when it is this user's turn: the loan is pending and the user belongs to the stage that must act. */
    public boolean canActOnCurrentStep(LoanActor me, LoanAdvance loan) {
        return actDeniedReason(me, loan) == null;
    }

    public void assertCanAct(LoanActor me, LoanAdvance loan) {
        String reason = actDeniedReason(me, loan);
        if (reason != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, reason);
        }
    }

    /** null = allowed, otherwise the message to show. */
    private String actDeniedReason(LoanActor me, LoanAdvance loan) {
        ApprovalChain chain = loan.getApproverChain();
        if (loan.getStatus() != LoanStatus.PENDING_APPROVAL || chain == null) {
            return "This loan request is not waiting for approval";
        }
        Optional<ApprovalStep> active = chain.activeStep();
        if (active.isEmpty()) {
            return "This loan request has no pending approval stage";
        }
        if (!allowSelfApproval && isOwner(me, loan.getEmpId())) {
            return "You cannot approve or reject your own loan request";
        }
        if (me.admin()) return null;

        boolean myTurn = switch (active.get().getRole()) {
            case REPORTING_MANAGER -> me.employeeCode().equals(chain.getReportingManagerCode());
            case HR_HEAD -> me.hr();
            case FINANCE -> me.finance();
        };
        return myTurn ? null
                : "Waiting for " + active.get().getTitle() + ". You can act only when this request reaches your stage";
    }

    private static boolean hasRole(String roleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || roleName == null) return false;
        String expected = "ROLE_" + roleName;
        return auth.getAuthorities().stream().anyMatch(a -> expected.equalsIgnoreCase(a.getAuthority()));
    }
}