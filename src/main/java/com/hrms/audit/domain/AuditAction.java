package com.hrms.audit.domain;

public final class AuditAction {
    public static final String CREATE   = "Create";
    public static final String UPDATE   = "Update";
    public static final String APPROVAL = "Approval";
    public static final String REJECT   = "Reject";
    public static final String DELETE   = "Delete";
    public static final String VIEW     = "View";
    public static final String UPLOAD   = "Upload";
    public static final String DOWNLOAD = "Download";
    public static final String LOGIN    = "Login";
    public static final String LOGOUT   = "Logout";
    // In AuditModule.java, add:
    public static final String MASTER = "Master Data";
    public static final String BRANCH = "Branch";

    private AuditAction() {}
}
