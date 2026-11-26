-- =========================================================================
-- HRMS Extension: Daily Task Reporting, Review & Performance Tracking
-- Migration: Additive Schema Creation (Non-destructive)
-- =========================================================================

CREATE TABLE IF NOT EXISTS task_daily_reports (
    id BIGSERIAL PRIMARY KEY,
    subtask_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    report_date DATE NOT NULL,
    day_number INT NOT NULL,
    planned_work TEXT,
    completed_work TEXT,
    work_description TEXT NOT NULL,
    hours_worked NUMERIC(4, 2) NOT NULL,
    completion_percentage INT NOT NULL CHECK (completion_percentage >= 0 AND completion_percentage <= 100),
    blockers TEXT,
    pending_work TEXT,
    next_day_plan TEXT,
    work_link VARCHAR(500),
    remarks TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    revision_number INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    submitted_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_daily_report_subtask FOREIGN KEY (subtask_id) REFERENCES tasks(id),
    CONSTRAINT fk_daily_report_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT uk_daily_report_emp_subtask_date UNIQUE (employee_id, subtask_id, report_date)
);

CREATE INDEX IF NOT EXISTS idx_daily_report_subtask ON task_daily_reports(subtask_id);
CREATE INDEX IF NOT EXISTS idx_daily_report_employee ON task_daily_reports(employee_id);
CREATE INDEX IF NOT EXISTS idx_daily_report_date ON task_daily_reports(report_date);
CREATE INDEX IF NOT EXISTS idx_daily_report_status ON task_daily_reports(status);

CREATE TABLE IF NOT EXISTS task_daily_reviews (
    id BIGSERIAL PRIMARY KEY,
    daily_report_id BIGINT NOT NULL,
    manager_id BIGINT NOT NULL,
    report_revision INT NOT NULL DEFAULT 1,
    rating VARCHAR(20),
    rating_score INT CHECK (rating_score IS NULL OR rating_score IN (1, 2, 3)),
    feedback TEXT NOT NULL,
    review_decision VARCHAR(50) NOT NULL,
    reviewed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_daily_review_report FOREIGN KEY (daily_report_id) REFERENCES task_daily_reports(id) ON DELETE CASCADE,
    CONSTRAINT fk_daily_review_manager FOREIGN KEY (manager_id) REFERENCES employees(id),
    CONSTRAINT uk_daily_review_report_rev UNIQUE (daily_report_id, report_revision)
);

CREATE INDEX IF NOT EXISTS idx_daily_review_report ON task_daily_reviews(daily_report_id);
CREATE INDEX IF NOT EXISTS idx_daily_review_manager ON task_daily_reviews(manager_id);
CREATE INDEX IF NOT EXISTS idx_daily_review_rating ON task_daily_reviews(rating_score);
