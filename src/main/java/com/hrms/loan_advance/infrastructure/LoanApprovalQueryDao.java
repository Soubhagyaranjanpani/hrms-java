package com.hrms.loan_advance.infrastructure;

import com.hrms.loan_advance.domain.LoanAdvance;
import com.hrms.loan_advance.domain.LoanApprovalTab;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds the query of the Approvals page (tabs + search + scope). The conditions change with every
 * request, so the SQL is assembled here with named parameters (never by joining user text into SQL).
 *
 * Status is compared with cast(status as text) because Postgres will not compare an enum column
 * with a varchar parameter. Who-must-act-now is read from the JSON in loan.approver_chain
 * (currentRole / currentApproverCode / reportingManagerCode).
 */
@Repository
public class LoanApprovalQueryDao {

    /** Who is asking and what they may see. seeAll = Admin, HR or Finance. */
    public record ApprovalScope(String viewerCode, boolean seeAll, boolean admin, boolean hr,
                                boolean finance, boolean allowSelfApproval) {
    }

    /** Optional free-text search. All parts are optional. */
    public record ApprovalSearch(String likeText, Long loanId, List<String> employeeCodes) {
        public static ApprovalSearch none() {
            return new ApprovalSearch(null, null, List.of());
        }

        boolean isEmpty() {
            return likeText == null && loanId == null && (employeeCodes == null || employeeCodes.isEmpty());
        }
    }

    @PersistenceContext
    private EntityManager entityManager;

    public Page<LoanAdvance> find(ApprovalScope scope, LoanApprovalTab tab, ApprovalSearch search, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        String where = buildWhere(scope, tab, search, params);

        Query countQuery = entityManager.createNativeQuery("select count(*) from loan l" + where);
        bind(countQuery, params);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        Query dataQuery = entityManager.createNativeQuery(
                "select l.* from loan l" + where + " order by l.loan_id desc", LoanAdvance.class);
        bind(dataQuery, params);
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<LoanAdvance> rows = dataQuery.getResultList();
        return new PageImpl<>(rows, pageable, total);
    }

    /** Number shown on a tab (search is not applied to tab counts). */
    public long count(ApprovalScope scope, LoanApprovalTab tab) {
        Map<String, Object> params = new HashMap<>();
        String where = buildWhere(scope, tab, ApprovalSearch.none(), params);
        Query query = entityManager.createNativeQuery("select count(*) from loan l" + where);
        bind(query, params);
        return ((Number) query.getSingleResult()).longValue();
    }

    private String buildWhere(ApprovalScope scope, LoanApprovalTab tab, ApprovalSearch search, Map<String, Object> params) {
        StringBuilder sql = new StringBuilder(" where 1 = 1");

        // Scope: Admin/HR/Finance see every loan; a reporting manager sees only their own team's loans.
        if (!scope.seeAll()) {
            sql.append(" and l.approver_chain ->> 'reportingManagerCode' = :viewer");
            params.put("viewer", scope.viewerCode());
        }

        switch (tab) {
            case PENDING_FOR_ME -> appendPendingForMe(sql, scope, params);
            case PENDING_APPROVAL -> sql.append(" and cast(l.status as text) = 'Pending Approval'");
            case APPROVED -> sql.append(" and cast(l.status as text) in ('Approved', 'Active')");
            case REJECTED -> sql.append(" and cast(l.status as text) = 'Rejected'");
            default -> { /* ALL: no extra condition */ }
        }

        if (!search.isEmpty()) {
            List<String> anyOf = new ArrayList<>();
            if (search.loanId() != null) {
                anyOf.add("l.loan_id = :searchLoanId");
                params.put("searchLoanId", search.loanId());
            }
            if (search.employeeCodes() != null && !search.employeeCodes().isEmpty()) {
                anyOf.add("l.emp_id in (:searchCodes)");
                params.put("searchCodes", search.employeeCodes());
            }
            if (search.likeText() != null) {
                anyOf.add("lower(cast(l.loan_type as text)) like :searchLike");
                anyOf.add("lower(cast(l.status as text)) like :searchLike");
                params.put("searchLike", search.likeText());
            }
            sql.append(" and (").append(String.join(" or ", anyOf)).append(")");
        }
        return sql.toString();
    }

    /** Pending loans whose current stage the viewer is allowed to act on. */
    private void appendPendingForMe(StringBuilder sql, ApprovalScope scope, Map<String, Object> params) {
        sql.append(" and cast(l.status as text) = 'Pending Approval'");
        if (!scope.allowSelfApproval()) {
            sql.append(" and l.emp_id <> :viewerSelf");
            params.put("viewerSelf", scope.viewerCode());
        }
        if (scope.admin()) {
            return; // Admin can act on every pending stage
        }
        List<String> myStages = new ArrayList<>();
        myStages.add("l.approver_chain ->> 'currentApproverCode' = :viewerStage");
        params.put("viewerStage", scope.viewerCode());
        if (scope.hr()) myStages.add("l.approver_chain ->> 'currentRole' = 'HR_HEAD'");
        if (scope.finance()) myStages.add("l.approver_chain ->> 'currentRole' = 'FINANCE'");
        sql.append(" and (").append(String.join(" or ", myStages)).append(")");
    }

    private static void bind(Query query, Map<String, Object> params) {
        params.forEach(query::setParameter);
    }
}