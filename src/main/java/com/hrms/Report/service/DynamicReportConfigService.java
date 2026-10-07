package com.hrms.Report.service;



import com.hrms.attendance.domain.Attendance;
import com.hrms.employee.domain.Employee;
import com.hrms.leave.domain.Leave;
import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.task.domain.Task;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.Role;
import com.hrms.attendance.domain.AttendanceStatus;
import com.hrms.leave.domain.enums.LeaveStatus;
import com.hrms.task.domain.TaskStatus;
import com.hrms.task.domain.TaskPriority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DynamicReportConfigService {

    /**
     * Dynamically discover all available report types
     */
    public List<Map<String, Object>> discoverReportTypes() {
        List<Map<String, Object>> reportTypes = new ArrayList<>();

        Map<String, Class<?>> reportEntities = new LinkedHashMap<>();
        reportEntities.put("EMPLOYEE_REPORT", Employee.class);
        reportEntities.put("ATTENDANCE_REPORT", Attendance.class);
        reportEntities.put("LEAVE_REPORT", Leave.class);
        reportEntities.put("PAYROLL_REPORT", PayrollRecord.class);
        reportEntities.put("TASK_REPORT", Task.class);
        reportEntities.put("DEPARTMENT_REPORT", Department.class);
        reportEntities.put("BRANCH_REPORT", Branch.class);

        for (Map.Entry<String, Class<?>> entry : reportEntities.entrySet()) {
            Map<String, Object> reportType = new LinkedHashMap<>();
            reportType.put("type", entry.getKey());
            reportType.put("name", getReportDisplayName(entry.getKey()));
            reportType.put("description", getReportDescription(entry.getKey()));
            reportType.put("icon", getReportIcon(entry.getKey()));
            reportType.put("availableFilters", getAvailableFilters(entry.getKey()));
            reportType.put("columns", getEntityColumns(entry.getValue()));
            reportTypes.add(reportType);
        }

        return reportTypes;
    }

    /**
     * Get columns from entity class dynamically using reflection
     */
    public List<Map<String, Object>> getEntityColumns(Class<?> entityClass) {
        List<Map<String, Object>> columns = new ArrayList<>();
        Field[] fields = getAllFields(entityClass);

        Set<String> excludedFields = new HashSet<>(Arrays.asList(
                "id", "password", "tempOtp", "otpExpiryTime", "isDeleted",
                "createdAt", "updatedAt", "createdBy", "updatedBy", "processedBy",
                "serialVersionUID", "manager", "subordinates", "parentTask",
                "subtasks", "lastLogin", "authorities"
        ));

        for (Field field : fields) {
            String fieldName = field.getName();

            if (excludedFields.contains(fieldName)) continue;
            if (Collection.class.isAssignableFrom(field.getType())) continue;
            if (isEntityReference(field)) continue;

            Map<String, Object> column = new LinkedHashMap<>();
            column.put("field", fieldName);
            column.put("header", formatColumnHeader(fieldName));
            column.put("type", detectFieldType(field));
            column.put("sortable", true);
            column.put("filterable", true);
            column.put("defaultVisible", isDefaultVisible(fieldName));

            if (field.getType().isEnum()) {
                column.put("enumValues", getEnumValues(field.getType()));
            }

            columns.add(column);
        }

        return columns;
    }

    private Field[] getAllFields(Class<?> clazz) {
        List<Field> allFields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            allFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return allFields.toArray(new Field[0]);
    }

    private boolean isEntityReference(Field field) {
        String typeName = field.getType().getName();
        return typeName.startsWith("com.hrms.") && !field.getType().isEnum() &&
                !typeName.contains("java.");
    }

    private String detectFieldType(Field field) {
        String name = field.getName().toLowerCase();
        Class<?> type = field.getType();

        if (type.isEnum()) return "STATUS";
        if (type == Boolean.class || type == boolean.class) return "BOOLEAN";
        if (name.contains("date") || name.contains("time")) return "DATE";
        if (name.contains("salary") || name.contains("amount") ||
                name.contains("earning") || name.contains("deduction") ||
                name.contains("pay") || name.contains("allow") || name.contains("hra")) {
            return "CURRENCY";
        }
        if (Number.class.isAssignableFrom(type) ||
                type == int.class || type == long.class ||
                type == double.class || type == float.class) {
            return "NUMBER";
        }
        return "TEXT";
    }

    private String formatColumnHeader(String fieldName) {
        return Arrays.stream(fieldName.split("(?=[A-Z])"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .reduce((a, b) -> a + " " + b)
                .orElse(fieldName);
    }

    private boolean isDefaultVisible(String fieldName) {
        Set<String> hiddenByDefault = new HashSet<>(Arrays.asList(
                "profilePicture", "address", "remarks", "aiInsight",
                "rejectionReason", "halfDaySession", "isLate", "isEarlyExit",
                "isManualEntry", "gradePay", "employerPF", "npsEmployer",
                "esiEmployer", "gratuityAccrual", "arrears", "noticePeriodPay",
                "leaveTravelAllowance", "telephoneAllowance", "tempOtp",
                "otpExpiryTime", "isAccountNonExpired", "isAccountNonLocked",
                "isCredentialsNonExpired", "isEnabled"
        ));
        return !hiddenByDefault.contains(fieldName);
    }

    private List<String> getEnumValues(Class<?> enumClass) {
        if (!enumClass.isEnum()) return new ArrayList<>();
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Object::toString)
                .toList();
    }

    private List<Map<String, Object>> getAvailableFilters(String reportType) {
        List<Map<String, Object>> filters = new ArrayList<>();

        // Date Range filter (common)
        filters.add(createFilterObj("dateRange", "Date Range", "DATE_RANGE"));

        // Search filter (common)
        filters.add(createFilterObj("searchTerm", "Search", "TEXT"));

        // Type-specific filters
        switch (reportType) {
            case "EMPLOYEE_REPORT":
                filters.add(createFilterObj("departmentId", "Department", "MULTI_SELECT", "/departments/list"));
                filters.add(createFilterObj("branchId", "Branch", "MULTI_SELECT", "/branches/list"));
                filters.add(createFilterObj("roleId", "Role", "MULTI_SELECT", "/roles/list"));
                filters.add(createFilterObjWithOptions("status", "Status", "MULTI_SELECT",
                        Arrays.asList("ACTIVE", "INACTIVE")));
                break;

            case "ATTENDANCE_REPORT":
                filters.add(createFilterObjWithOptions("statuses", "Status", "MULTI_SELECT",
                        getEnumValues(AttendanceStatus.class)));
                filters.add(createFilterObj("employeeId", "Employee", "MULTI_SELECT", "/api/employees"));
                break;

            case "LEAVE_REPORT":
                filters.add(createFilterObjWithOptions("statuses", "Status", "MULTI_SELECT",
                        getEnumValues(LeaveStatus.class)));
                filters.add(createFilterObj("leaveTypeId", "Leave Type", "MULTI_SELECT", "/api/leave-types"));
                break;

            case "PAYROLL_REPORT":
                filters.add(createFilterObjWithOptions("statuses", "Status", "MULTI_SELECT",
                        Arrays.asList("DRAFT", "PENDING", "APPROVED", "PROCESSED", "PAID", "REJECTED")));
                filters.add(createFilterObj("month", "Month", "MONTH"));
                break;

            case "TASK_REPORT":
                filters.add(createFilterObjWithOptions("statuses", "Status", "MULTI_SELECT",
                        getEnumValues(TaskStatus.class)));
                filters.add(createFilterObjWithOptions("priorities", "Priority", "MULTI_SELECT",
                        getEnumValues(TaskPriority.class)));
                break;
        }

        return filters;
    }

    private Map<String, Object> createFilterObj(String field, String label, String type) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put("field", field);
        filter.put("label", label);
        filter.put("type", type);
        filter.put("required", false);
        return filter;
    }

    private Map<String, Object> createFilterObj(String field, String label, String type, String dataSource) {
        Map<String, Object> filter = createFilterObj(field, label, type);
        filter.put("dataSource", dataSource);
        return filter;
    }

    private Map<String, Object> createFilterObjWithOptions(String field, String label, String type, List<String> options) {
        Map<String, Object> filter = createFilterObj(field, label, type);
        filter.put("options", options);
        return filter;
    }

    private String getReportDisplayName(String reportType) {
        return switch (reportType) {
            case "EMPLOYEE_REPORT" -> "Employee Directory";
            case "ATTENDANCE_REPORT" -> "Attendance Records";
            case "LEAVE_REPORT" -> "Leave Management";
            case "PAYROLL_REPORT" -> "Payroll Summary";
            case "TASK_REPORT" -> "Task Management";
            case "DEPARTMENT_REPORT" -> "Department Summary";
            case "BRANCH_REPORT" -> "Branch Overview";
            default -> reportType.replace("_", " ");
        };
    }

    private String getReportDescription(String reportType) {
        return switch (reportType) {
            case "EMPLOYEE_REPORT" -> "Complete employee directory with all details";
            case "ATTENDANCE_REPORT" -> "Daily attendance records and statistics";
            case "LEAVE_REPORT" -> "Leave requests and approval tracking";
            case "PAYROLL_REPORT" -> "Salary details and payroll processing";
            case "TASK_REPORT" -> "Task assignments and progress tracking";
            case "DEPARTMENT_REPORT" -> "Department-wise employee distribution";
            case "BRANCH_REPORT" -> "Branch-wise employee summary";
            default -> "Generated report";
        };
    }

    private String getReportIcon(String reportType) {
        return switch (reportType) {
            case "EMPLOYEE_REPORT" -> "users";
            case "ATTENDANCE_REPORT" -> "clock";
            case "LEAVE_REPORT" -> "calendar";
            case "PAYROLL_REPORT" -> "rupee";
            case "TASK_REPORT" -> "tasks";
            case "DEPARTMENT_REPORT" -> "building";
            case "BRANCH_REPORT" -> "home";
            default -> "chart";
        };
    }
}
