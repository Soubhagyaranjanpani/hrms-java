package com.hrms.appointment.application;

import com.hrms.appointment.domain.AppointmentRecord;
import com.hrms.appointment.dto.AppointmentRecordResponse;
import com.hrms.appointment.dto.CreateAppointmentRequest;
import com.hrms.appointment.infrastructure.AppointmentRepository;
import com.hrms.audit.application.AuditService;
import com.hrms.audit.domain.AuditAction;
import com.hrms.audit.domain.AuditModule;
import com.hrms.employee.domain.Employee;
import com.hrms.employee.domain.EmployeeDesignation;
import com.hrms.employee.infrastructure.EmployeeDesignationRepository;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.employment_type.domain.EmploymentType;
import com.hrms.employment_type.infrastructure.EmploymentTypeRepository;
import com.hrms.master.domain.AppointmentType;
import com.hrms.master.domain.Branch;
import com.hrms.master.domain.Department;
import com.hrms.master.domain.Designation;
import com.hrms.master.infrastructure.AppointmentTypeRepository;
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
    private final AppointmentTypeRepository appointmentTypeRepo;
    private final EmploymentTypeRepository employmentTypeRepo;
    private final AppointmentMapper mapper;
    private final PdfAppointmentLetterGenerator letterGenerator;
    private final AppointmentDocumentStorageService storageService;
    private final AuditService auditService;

    public AppointmentRecordResponse execute(CreateAppointmentRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        AppointmentRecord r = new AppointmentRecord();
        r.setEmployee(emp);
        r.setAppointmentOrderNumber(req.getAppointmentOrderNumber());

        // ── Initial designation / department / branch ──
        Designation initialDesig = designationRepo.findById(req.getInitialDesignationId())
                .orElseThrow(() -> new RuntimeException("Initial designation not found"));
        r.setInitialDesignation(initialDesig);

        Department initialDept = departmentRepo.findById(req.getInitialDepartmentId())
                .orElseThrow(() -> new RuntimeException("Initial department not found"));
        r.setInitialDepartment(initialDept);

        Branch initialBranch = branchRepo.findById(req.getInitialBranchId())
                .orElseThrow(() -> new RuntimeException("Initial branch not found"));
        r.setInitialBranch(initialBranch);

        // ── Appointment type / Employment type ──
        AppointmentType appointmentType = appointmentTypeRepo.findById(req.getAppointmentTypeId())
                .orElseThrow(() -> new RuntimeException("Appointment type not found"));
        r.setAppointmentType(appointmentType);

        EmploymentType employmentType = employmentTypeRepo.findById(req.getEmploymentTypeId())
                .orElseThrow(() -> new RuntimeException("Employment type not found"));
        r.setEmploymentType(employmentType);

        // ── Dates / probation ──
        LocalDate date = req.getAppointmentDate() != null ? req.getAppointmentDate() : LocalDate.now();
        r.setAppointmentDate(date);
        r.setJoiningDate(req.getJoiningDate() != null ? req.getJoiningDate() : date);
        r.setProbationPeriodMonths(req.getProbationPeriodMonths() != null ? req.getProbationPeriodMonths() : 6);

        // ── Authority ──
        EmployeeDesignation authority = employeeDesignationRepo.findById(req.getAppointmentAuthorityId())
                .orElseThrow(() -> new RuntimeException("Appointment authority not found"));
        r.setAppointmentAuthority(authority);

        r.setRemarks(req.getRemarks());
        r.setIsActive(true);

        r.compute(); // fills confirmationDueDate

        AppointmentRecord saved = appointmentRepo.save(r);

        // Update the employee's live department/branch
        String oldDepartment = emp.getDepartment() != null ? emp.getDepartment().getName() : "None";
        String oldBranch = emp.getBranch() != null ? emp.getBranch().getName() : "None";

        emp.setDepartment(initialDept);
        emp.setBranch(initialBranch);
        empRepo.save(emp);

        // Auto-generate the appointment letter
        boolean letterGenerated = false;
        try {
            byte[] pdfBytes = letterGenerator.generateLetter(saved);
            String path = storageService.saveGenerated(saved.getId(), emp.getEmployeeCode(), pdfBytes);

            saved.setDocumentPath(path);
            saved.setDocumentName(storageService.fileNameOf(path));
            saved = appointmentRepo.save(saved);
            letterGenerated = true;
        } catch (Exception e) {
            System.err.println("Failed to auto-generate appointment letter for id " + saved.getId() + ": " + e.getMessage());
        }

        // Audit Log 1 - Main appointment creation
        auditService.log(
                AuditModule.APPOINTMENT,
                AuditAction.CREATE,
                "Appointment created: " + saved.getAppointmentOrderNumber() +
                        " for " + emp.getFullName() + " (" + emp.getEmployeeCode() + ")",
                emp,
                "Appointment Record",
                null,
                "Designation: " + initialDesig.getName() +
                        ", Type: " + appointmentType.getAppointmentType(),
                "New appointment record created",
                saved.getId()
        );

        // Audit Log 2 - Employee department/branch update
        if (!oldDepartment.equals(initialDept.getName()) || !oldBranch.equals(initialBranch.getName())) {
            auditService.log(
                    AuditModule.EMPLOYEE,
                    AuditAction.UPDATE,
                    "Employee department/branch updated due to appointment",
                    emp,
                    "Department/Branch",
                    oldDepartment + " / " + oldBranch,
                    initialDept.getName() + " / " + initialBranch.getName(),
                    "Updated due to appointment: " + saved.getAppointmentOrderNumber(),
                    emp.getId()
            );
        }

        // Audit Log 3 - Document generation (if successful)
        if (letterGenerated) {
            auditService.log(
                    AuditModule.DOCUMENTS,
                    AuditAction.UPLOAD,
                    "Appointment letter generated: " + saved.getDocumentName(),
                    emp,
                    "Document",
                    null,
                    saved.getDocumentName(),
                    "Auto-generated appointment letter",
                    saved.getId()
            );
        }

        return mapper.toResponse(saved);
    }
}