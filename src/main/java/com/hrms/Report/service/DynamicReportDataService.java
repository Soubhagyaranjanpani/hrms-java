package com.hrms.Report.service;

import com.hrms.Report.dto.ReportRequestDTO;
import com.hrms.Report.service.DynamicReportConfigService;
import com.hrms.attendance.domain.Attendance;
import com.hrms.attendance.infrastructure.AttendanceRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.leave.domain.Leave;
import com.hrms.leave.infrastructure.LeaveRepository;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;

import com.hrms.master.infrastructure.BranchRepository;
import com.hrms.master.infrastructure.DepartmentRepository;
import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.payroll.infrastructure.PayrollRepository;
import com.hrms.task.domain.Task;
import com.hrms.task.infrastructure.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicReportDataService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final PayrollRepository payrollRecordRepository;
    private final TaskRepository taskRepository;
    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;
    private final DynamicReportConfigService configService;

    public List<Map<String, Object>> fetchReportData(ReportRequestDTO request) {
        return switch (request.getReportType()) {
            case "EMPLOYEE_REPORT" -> mapToDynamicRows(fetchEmployees(request), Employee.class);
            case "ATTENDANCE_REPORT" -> mapToDynamicRows(fetchAttendance(request), Attendance.class);
            case "LEAVE_REPORT" -> mapToDynamicRows(fetchLeaves(request), Leave.class);
            case "PAYROLL_REPORT" -> mapToDynamicRows(fetchPayroll(request), PayrollRecord.class);
            case "TASK_REPORT" -> mapToDynamicRows(fetchTasks(request), Task.class);
            case "DEPARTMENT_REPORT" -> mapToDynamicRows(fetchDepartments(request), Department.class);
            case "BRANCH_REPORT" -> mapToDynamicRows(fetchBranches(request), Branch.class);
            default -> new ArrayList<>();
        };
    }

    private List<Map<String, Object>> mapToDynamicRows(List<?> entities, Class<?> entityClass) {
        List<Map<String, Object>> rows = new ArrayList<>();
        List<Map<String, Object>> columns = configService.getEntityColumns(entityClass);
        List<String> fieldNames = columns.stream()
                .map(col -> (String) col.get("field"))
                .collect(Collectors.toList());

        for (Object entity : entities) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (String fieldName : fieldNames) {
                try {
                    Object value = getFieldValue(entity, fieldName);
                    value = resolveEntityValue(value);
                    row.put(fieldName, value);
                } catch (Exception e) {
                    row.put(fieldName, null);
                }
            }
            rows.add(row);
        }
        return rows;
    }

    private Object getFieldValue(Object entity, String fieldName) throws Exception {
        // Try getter method
        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            Method getter = entity.getClass().getMethod(getterName);
            return getter.invoke(entity);
        } catch (NoSuchMethodException e) {
            // Try boolean getter (isXxx)
            String isGetterName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            try {
                Method isGetter = entity.getClass().getMethod(isGetterName);
                return isGetter.invoke(entity);
            } catch (NoSuchMethodException ex) {
                // Try field directly
                Field field = findField(entity.getClass(), fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    return field.get(entity);
                }
            }
        }
        return null;
    }

    private Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    private Object resolveEntityValue(Object value) {
        if (value == null) return null;
        if (value instanceof Employee emp) {
            return emp.getFirstName() + " " + (emp.getLastName() != null ? emp.getLastName() : "");
        }
        if (value instanceof Department dept) return dept.getName();
        if (value instanceof Branch branch) return branch.getName();
        if (value instanceof com.hrms.master.domain.Role role) return role.getName();
        if (value instanceof com.hrms.leave.domain.LeaveType lt) return lt.getName();
        if (value.getClass().getName().startsWith("com.hrms.") && !value.getClass().isEnum()) {
            try {
                Method getNameMethod = value.getClass().getMethod("getName");
                return getNameMethod.invoke(value);
            } catch (Exception e) {
                return value.toString();
            }
        }
        return value;
    }

    private List<Employee> fetchEmployees(ReportRequestDTO request) {
        List<Employee> employees = employeeRepository.findByIsDeletedFalse();
        return applyCommonFilters(employees, request);
    }

    private List<Attendance> fetchAttendance(ReportRequestDTO request) {
        List<Attendance> records;
        if (request.getStartDate() != null && request.getEndDate() != null) {
            records = attendanceRepository.findByDateBetween(request.getStartDate(), request.getEndDate());
        } else if (request.getMonth() != null) {
            LocalDate start = LocalDate.parse(request.getMonth() + "-01");
            LocalDate end = start.plusMonths(1).minusDays(1);
            records = attendanceRepository.findByDateBetween(start, end);
        } else {
            records = attendanceRepository.findAll();
        }
        records = records.stream().filter(a -> !a.getIsDeleted()).collect(Collectors.toList());
        return applyCommonFilters(records, request);
    }

    private List<Leave> fetchLeaves(ReportRequestDTO request) {
        List<Leave> leaves;
        if (request.getStartDate() != null && request.getEndDate() != null) {
            leaves = leaveRepository.findByStartDateBetweenAndIsDeletedFalse(request.getStartDate(), request.getEndDate());
        } else {
            leaves = leaveRepository.findByIsDeletedFalse();
        }
        return applyCommonFilters(leaves, request);
    }

    private List<PayrollRecord> fetchPayroll(ReportRequestDTO request) {
        List<PayrollRecord> records;
        if (request.getMonth() != null) {
            records = payrollRecordRepository.findByYearMonth(request.getMonth());
        } else {
            records = payrollRecordRepository.findAll();
        }
        records = records.stream().filter(r -> !r.getIsDeleted()).collect(Collectors.toList());
        return applyCommonFilters(records, request);
    }

    private List<Task> fetchTasks(ReportRequestDTO request) {
        List<Task> tasks = taskRepository.findByIsDeletedFalse();
        return applyCommonFilters(tasks, request);
    }

    private List<Department> fetchDepartments(ReportRequestDTO request) {
        return departmentRepository.findAll();
    }

    private List<Branch> fetchBranches(ReportRequestDTO request) {
        return branchRepository.findAll();
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> applyCommonFilters(List<T> list, ReportRequestDTO request) {
        if (request.getStatuses() != null && !request.getStatuses().isEmpty()) {
            list = list.stream().filter(item -> {
                try {
                    Object status = getFieldValue(item, "status");
                    return status != null && request.getStatuses().contains(status.toString());
                } catch (Exception e) { return false; }
            }).collect(Collectors.toList());
        }
        if (request.getSearchTerm() != null && !request.getSearchTerm().isEmpty()) {
            String search = request.getSearchTerm().toLowerCase();
            list = list.stream().filter(item -> matchesSearch(item, search)).collect(Collectors.toList());
        }
        return list;
    }

    private boolean matchesSearch(Object entity, String search) {
        try {
            Field[] fields = getAllFields(entity.getClass());
            for (Field field : fields) {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    if (value != null && value.toString().toLowerCase().contains(search)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    private Field[] getAllFields(Class<?> clazz) {
        List<Field> allFields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            allFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return allFields.toArray(new Field[0]);
    }
}