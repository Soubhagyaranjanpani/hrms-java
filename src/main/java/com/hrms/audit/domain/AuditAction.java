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

    private AuditAction() {}
}