package com.hrms.taskprogress;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.Role;
import com.hrms.task.domain.Task;
import com.hrms.task.infrastructure.TaskRepository;
import com.hrms.taskprogress.application.*;
import com.hrms.taskprogress.domain.TaskDailyReport;
import com.hrms.taskprogress.domain.TaskDailyReview;
import com.hrms.taskprogress.domain.enums.PerformanceRating;
import com.hrms.taskprogress.domain.enums.ReportStatus;
import com.hrms.taskprogress.domain.enums.ReviewDecision;
import com.hrms.taskprogress.dto.DailyReportRequest;
import com.hrms.taskprogress.dto.DailyReportResponse;
import com.hrms.taskprogress.dto.DailyReviewRequest;
import com.hrms.taskprogress.engine.PerformanceCalculationEngine;
import com.hrms.taskprogress.infrastructure.TaskDailyReportRepository;
import com.hrms.taskprogress.infrastructure.TaskDailyReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskProgressServiceTest {

    @Mock
    private TaskDailyReportRepository dailyReportRepo;

    @Mock
    private TaskDailyReviewRepository dailyReviewRepo;

    @Mock
    private TaskRepository taskRepo;

    @Mock
    private EmployeeRepository employeeRepo;

    @Spy
    private TaskProgressMapper mapper = new TaskProgressMapper();

    @Spy
    private PerformanceCalculationEngine perfEngine = new PerformanceCalculationEngine();

    private Employee employee;
    private Employee manager;
    private Employee unauthorizedUser;
    private Task subtask;
    private Task parentTask;

    @BeforeEach
    void setUp() {
        Role empRole = new Role();
        empRole.setId(4L);
        empRole.setName("Employee");

        Role mgrRole = new Role();
        mgrRole.setId(3L);
        mgrRole.setName("Manager");

        manager = new Employee();
        manager.setId(10L);
        manager.setEmail("manager@hrms.com");
        manager.setFirstName("Rajesh");
        manager.setLastName("Kumar");
        manager.setRole(mgrRole);

        employee = new Employee();
        employee.setId(20L);
        employee.setEmail("emp@hrms.com");
        employee.setFirstName("Amit");
        employee.setLastName("Sharma");
        employee.setRole(empRole);
        employee.setManager(manager);

        unauthorizedUser = new Employee();
        unauthorizedUser.setId(30L);
        unauthorizedUser.setEmail("other@hrms.com");
        unauthorizedUser.setFirstName("Other");
        unauthorizedUser.setLastName("User");
        unauthorizedUser.setRole(empRole);

        parentTask = new Task();
        parentTask.setId(100L);
        parentTask.setTitle("HRMS Main Module");
        parentTask.setAssignedBy(manager);

        subtask = new Task();
        subtask.setId(101L);
        subtask.setTitle("Design Database Schema");
        subtask.setParentTask(parentTask);
        subtask.setAssignedTo(employee);
        subtask.setAssignedBy(manager);
        subtask.setStartDate(LocalDateTime.now().minusDays(5));
        subtask.setDueDate(LocalDateTime.now().plusDays(5));
    }

    @Test
    @DisplayName("1. Employee submits a valid daily report successfully")
    void testEmployeeSubmitsValidReport() {
        SubmitDailyReportUseCase useCase = new SubmitDailyReportUseCase(
                dailyReportRepo, taskRepo, employeeRepo, mapper
        );

        DailyReportRequest req = new DailyReportRequest();
        req.setSubtaskId(101L);
        req.setReportDate(LocalDate.now());
        req.setDayNumber(1);
        req.setPlannedWork("Design tables");
        req.setCompletedWork("Tables created");
        req.setWorkDescription("Created task_daily_reports and task_daily_reviews");
        req.setHoursWorked(new BigDecimal("8.00"));
        req.setCompletionPercentage(40);

        when(employeeRepo.findByEmail("emp@hrms.com")).thenReturn(Optional.of(employee));
        when(taskRepo.findById(101L)).thenReturn(Optional.of(subtask));
        when(dailyReportRepo.findByEmployeeIdAndSubtaskIdAndReportDate(20L, 101L, LocalDate.now()))
                .thenReturn(Optional.empty());
        when(dailyReportRepo.save(any(TaskDailyReport.class))).thenAnswer(inv -> {
            TaskDailyReport r = inv.getArgument(0);
            r.setId(500L);
            return r;
        });

        DailyReportResponse resp = useCase.execute(req, "emp@hrms.com");

        assertNotNull(resp);
        assertEquals(ReportStatus.SUBMITTED, resp.getStatus());
        assertEquals(new BigDecimal("8.00"), resp.getHoursWorked());
        assertEquals(40, resp.getCompletionPercentage());
        verify(dailyReportRepo, times(1)).save(any(TaskDailyReport.class));
    }

    @Test
    @DisplayName("2. Employee attempts to submit a duplicate report on the same date")
    void testEmployeeDuplicateReportFails() {
        SubmitDailyReportUseCase useCase = new SubmitDailyReportUseCase(
                dailyReportRepo, taskRepo, employeeRepo, mapper
        );

        DailyReportRequest req = new DailyReportRequest();
        req.setSubtaskId(101L);
        req.setReportDate(LocalDate.now());
        req.setWorkDescription("Duplicate");
        req.setHoursWorked(new BigDecimal("8.00"));
        req.setCompletionPercentage(50);

        TaskDailyReport existingApproved = new TaskDailyReport();
        existingApproved.setId(1L);
        existingApproved.setStatus(ReportStatus.APPROVED);

        when(employeeRepo.findByEmail("emp@hrms.com")).thenReturn(Optional.of(employee));
        when(taskRepo.findById(101L)).thenReturn(Optional.of(subtask));
        when(dailyReportRepo.findByEmployeeIdAndSubtaskIdAndReportDate(20L, 101L, LocalDate.now()))
                .thenReturn(Optional.of(existingApproved));

        assertThrows(IllegalStateException.class, () -> useCase.execute(req, "emp@hrms.com"));
    }

    @Test
    @DisplayName("3. Employee attempts to submit report for another employee's subtask")
    void testEmployeeSubmitsAnotherEmployeeReportFails() {
        SubmitDailyReportUseCase useCase = new SubmitDailyReportUseCase(
                dailyReportRepo, taskRepo, employeeRepo, mapper
        );

        DailyReportRequest req = new DailyReportRequest();
        req.setSubtaskId(101L);
        req.setReportDate(LocalDate.now());
        req.setWorkDescription("Unauthorized");
        req.setHoursWorked(new BigDecimal("8.00"));
        req.setCompletionPercentage(50);

        when(employeeRepo.findByEmail("other@hrms.com")).thenReturn(Optional.of(unauthorizedUser));
        when(taskRepo.findById(101L)).thenReturn(Optional.of(subtask)); // subtask assigned to employee (id 20), not unauthorizedUser (id 30)

        assertThrows(SecurityException.class, () -> useCase.execute(req, "other@hrms.com"));
    }

    @Test
    @DisplayName("4. Employee attempts to submit on a future date")
    void testEmployeeSubmitFutureDateFails() {
        SubmitDailyReportUseCase useCase = new SubmitDailyReportUseCase(
                dailyReportRepo, taskRepo, employeeRepo, mapper
        );

        DailyReportRequest req = new DailyReportRequest();
        req.setSubtaskId(101L);
        req.setReportDate(LocalDate.now().plusDays(2)); // future date
        req.setWorkDescription("Future date report");
        req.setHoursWorked(new BigDecimal("8.00"));
        req.setCompletionPercentage(50);

        when(employeeRepo.findByEmail("emp@hrms.com")).thenReturn(Optional.of(employee));
        when(taskRepo.findById(101L)).thenReturn(Optional.of(subtask));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(req, "emp@hrms.com"));
    }

    @Test
    @DisplayName("5. Manager approves an authorized report with valid rating and feedback")
    void testManagerApprovesReport() {
        ReviewDailyReportUseCase useCase = new ReviewDailyReportUseCase(
                dailyReportRepo, dailyReviewRepo, employeeRepo, mapper
        );

        TaskDailyReport report = new TaskDailyReport();
        report.setId(500L);
        report.setSubtask(subtask);
        report.setEmployee(employee);
        report.setStatus(ReportStatus.SUBMITTED);
        report.setRevisionNumber(1);
        report.setReviews(new ArrayList<>());

        DailyReviewRequest req = new DailyReviewRequest();
        req.setDecision(ReviewDecision.APPROVED);
        req.setRating(PerformanceRating.HIGH);
        req.setFeedback("Excellent delivery on database schema!");

        when(employeeRepo.findByEmail("manager@hrms.com")).thenReturn(Optional.of(manager));
        when(dailyReportRepo.findByIdWithLock(500L)).thenReturn(Optional.of(report));
        when(dailyReportRepo.save(any(TaskDailyReport.class))).thenAnswer(inv -> inv.getArgument(0));

        DailyReportResponse response = useCase.execute(500L, req, "manager@hrms.com");

        assertNotNull(response);
        assertEquals(ReportStatus.APPROVED, response.getStatus());
        verify(dailyReviewRepo, times(1)).save(any(TaskDailyReview.class));
    }

    @Test
    @DisplayName("6. Unauthorized manager attempts approval")
    void testUnauthorizedManagerApprovalFails() {
        ReviewDailyReportUseCase useCase = new ReviewDailyReportUseCase(
                dailyReportRepo, dailyReviewRepo, employeeRepo, mapper
        );

        TaskDailyReport report = new TaskDailyReport();
        report.setId(500L);
        report.setSubtask(subtask);
        report.setEmployee(employee);
        report.setStatus(ReportStatus.SUBMITTED);

        DailyReviewRequest req = new DailyReviewRequest();
        req.setDecision(ReviewDecision.APPROVED);
        req.setRating(PerformanceRating.HIGH);
        req.setFeedback("Unauthorized approval attempt");

        when(employeeRepo.findByEmail("other@hrms.com")).thenReturn(Optional.of(unauthorizedUser));
        when(dailyReportRepo.findByIdWithLock(500L)).thenReturn(Optional.of(report));

        assertThrows(SecurityException.class, () -> useCase.execute(500L, req, "other@hrms.com"));
    }

    @Test
    @DisplayName("7. Manager requests correction and employee resubmits")
    void testManagerRequestsCorrectionAndResubmit() {
        ReviewDailyReportUseCase reviewUseCase = new ReviewDailyReportUseCase(
                dailyReportRepo, dailyReviewRepo, employeeRepo, mapper
        );
        ResubmitDailyReportUseCase resubmitUseCase = new ResubmitDailyReportUseCase(
                dailyReportRepo, employeeRepo, mapper
        );

        TaskDailyReport report = new TaskDailyReport();
        report.setId(500L);
        report.setSubtask(subtask);
        report.setEmployee(employee);
        report.setStatus(ReportStatus.SUBMITTED);
        report.setRevisionNumber(1);
        report.setReviews(new ArrayList<>());

        // Step A: Manager requests correction
        DailyReviewRequest req = new DailyReviewRequest();
        req.setDecision(ReviewDecision.CORRECTION_REQUIRED);
        req.setFeedback("Please elaborate on the performance tuning details.");

        when(employeeRepo.findByEmail("manager@hrms.com")).thenReturn(Optional.of(manager));
        when(dailyReportRepo.findByIdWithLock(500L)).thenReturn(Optional.of(report));
        when(dailyReportRepo.save(any(TaskDailyReport.class))).thenAnswer(inv -> inv.getArgument(0));

        DailyReportResponse reviewResp = reviewUseCase.execute(500L, req, "manager@hrms.com");
        assertEquals(ReportStatus.CORRECTION_REQUIRED, reviewResp.getStatus());

        // Step B: Employee resubmits corrected report
        DailyReportRequest resubmitReq = new DailyReportRequest();
        resubmitReq.setWorkDescription("Detailed performance tuning notes added with index benchmarks.");
        resubmitReq.setHoursWorked(new BigDecimal("8.00"));
        resubmitReq.setCompletionPercentage(60);

        when(employeeRepo.findByEmail("emp@hrms.com")).thenReturn(Optional.of(employee));
        when(dailyReportRepo.findById(500L)).thenReturn(Optional.of(report));

        DailyReportResponse resubmitResp = resubmitUseCase.execute(500L, resubmitReq, "emp@hrms.com");
        assertEquals(ReportStatus.SUBMITTED, resubmitResp.getStatus());
        assertEquals(2, resubmitResp.getRevisionNumber());
    }

    @Test
    @DisplayName("8. Performance Calculation Engine correctly handles Average Rating, Normalized %, and Division by Zero")
    void testPerformanceCalculationEngine() {
        // Create reviews matching user prompt example:
        // Day 1: LOW = 1
        // Day 2: MEDIUM = 2
        // Day 3: HIGH = 3
        // Day 4: MEDIUM = 2
        // Day 5: HIGH = 3
        // Total Score = 11, Average = 2.20, Normalized % = 73.33%

        List<TaskDailyReview> reviews = new ArrayList<>();
        int[] scores = {1, 2, 3, 2, 3};
        PerformanceRating[] ratings = {
                PerformanceRating.LOW,
                PerformanceRating.MEDIUM,
                PerformanceRating.HIGH,
                PerformanceRating.MEDIUM,
                PerformanceRating.HIGH
        };

        for (int i = 0; i < scores.length; i++) {
            TaskDailyReview rev = new TaskDailyReview();
            rev.setReviewDecision(ReviewDecision.APPROVED);
            rev.setRating(ratings[i]);
            rev.setRatingScore(scores[i]);
            reviews.add(rev);
        }

        Double avg = perfEngine.calculateAverageRating(reviews);
        Double norm = perfEngine.calculateNormalizedRatingPercentage(reviews);

        assertNotNull(avg);
        assertNotNull(norm);
        assertEquals(2.20, avg, 0.001);
        assertEquals(73.33, norm, 0.01);

        // Division by Zero check
        Double emptyAvg = perfEngine.calculateAverageRating(List.of());
        Double emptyNorm = perfEngine.calculateNormalizedRatingPercentage(List.of());
        assertNull(emptyAvg);
        assertNull(emptyNorm);

        // Compliance check
        Double compliance = perfEngine.calculateReportingCompliance(5, 5);
        assertEquals(100.00, compliance);

        Double partialCompliance = perfEngine.calculateReportingCompliance(8, 10);
        assertEquals(80.00, partialCompliance);
    }
}
