package com.hrms.appointment.application;

import com.hrms.appointment.domain.AppointmentRecord;
import com.hrms.appointment.dto.AppointmentRecordResponse;
import com.hrms.appointment.dto.CreateAppointmentRequest;
import com.hrms.appointment.infrastructure.AppointmentRepository;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.Designation;
import com.hrms.master.infrastructure.BranchRepository;
import com.hrms.master.infrastructure.DepartmentRepository;
import com.hrms.master.infrastructure.DesignationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepo;
    private final EmployeeRepository empRepo;
    private final DesignationRepository designationRepo;
    private final DepartmentRepository departmentRepo;
    private final BranchRepository branchRepo;
    private final EmployeeDesignationRepository employeeDesignationRepo;
    private final AppointmentMapper mapper;
    private final PdfAppointmentLetterGenerator letterGenerator;
    private final AppointmentDocumentStorageService storageService;

    public AppointmentRecordResponse execute(CreateAppointmentRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        AppointmentRecord r = new AppointmentRecord();
        r.setEmployee(emp);
        r.setAppointmentOrderNumber(req.getAppointmentOrderNumber());

        // ── Initial designation / department / branch ──
        // Unlike Promotion, an appointment has no "old value" to fall back on — these
        // are always the values chosen in the form's Initial Designation/Department/Branch dropdowns.
        Designation initialDesig = designationRepo.findById(req.getInitialDesignationId())
                .orElseThrow(() -> new RuntimeException("Initial designation not found"));
        r.setInitialDesignation(initialDesig);

        Department initialDept = departmentRepo.findById(req.getInitialDepartmentId())
                .orElseThrow(() -> new RuntimeException("Initial department not found"));
        r.setInitialDepartment(initialDept);

        Branch initialBranch = branchRepo.findById(req.getInitialBranchId())
                .orElseThrow(() -> new RuntimeException("Initial branch not found"));
        r.setInitialBranch(initialBranch);

        r.setAppointmentType(req.getAppointmentType());
        r.setEmploymentType(req.getEmploymentType());

        // ── Dates / probation ──
        LocalDate date = req.getAppointmentDate() != null ? req.getAppointmentDate() : LocalDate.now();
        r.setAppointmentDate(date);
        r.setJoiningDate(req.getJoiningDate() != null ? req.getJoiningDate() : date);
        r.setProbationPeriodMonths(req.getProbationPeriodMonths() != null ? req.getProbationPeriodMonths() : 6);

        // ── Authority ── (an EmployeeDesignation — a person holding a role, not a plain Designation)
        EmployeeDesignation authority = employeeDesignationRepo.findById(req.getAppointmentAuthorityId())
                .orElseThrow(() -> new RuntimeException("Appointment authority not found"));
        r.setAppointmentAuthority(authority);

        r.setRemarks(req.getRemarks());
        r.setIsActive(true);

        r.compute(); // fills confirmationDueDate

        AppointmentRecord saved = appointmentRepo.save(r);

        // Update the employee's live department/branch to reflect this appointment immediately.
        // NOTE: if the Employee's *current designation* also needs to be set from this appointment,
        // that goes through the EmployeeDesignation join entity (as findFirstByEmployee_IdAndIsActiveTrueAndIsDeletedFalse
        // is used elsewhere to read it) — wire that creation in here once you share that entity's fields,
        // since guessing its columns risks breaking the build.
        emp.setDepartment(initialDept);
        emp.setBranch(initialBranch);
        empRepo.save(emp);

        // Auto-generate the appointment letter and persist its path/name on the record
        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = appointmentRepo.save(saved);
        } catch (Exception e) {
            System.err.println("Failed to auto-generate appointment letter for id " + saved.getId() + ": " + e.getMessage());
        }

        return mapper.toResponse(saved);
    }
}


