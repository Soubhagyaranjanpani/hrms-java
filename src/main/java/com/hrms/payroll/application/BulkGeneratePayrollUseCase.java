package com.hrms.payroll.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.payroll.domain.SalaryStructure;
import com.hrms.payroll.dto.BulkGenerateRequest;
import com.hrms.payroll.infrastructure.PayrollRepository;
import com.hrms.payroll.infrastructure.SalaryStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkGeneratePayrollUseCase {

    private final PayrollRepository         payrollRepo;
    private final EmployeeRepository        empRepo;
    private final SalaryStructureRepository structRepo;

    public String execute(BulkGenerateRequest req) {
        List<Employee> employees = empRepo.findByIsActiveTrueAndIsDeletedFalse();
        String ym    = req.getYearMonth();
        String label = formatMonth(ym);
        int    created = 0, skipped = 0;

        for (Employee emp : employees) {
            if (payrollRepo.findByEmployee_IdAndYearMonthAndIsDeletedFalse(emp.getId(), ym).isPresent()) {
                skipped++; continue;
            }
            PayrollRecord r = new PayrollRecord();
            r.setEmployee(emp);
            r.setYearMonth(ym);
            r.setPayrollMonth(label);
            r.setWorkingDays(req.getWorkingDays() != null ? req.getWorkingDays() : 26);
            r.setPaidDays(r.getWorkingDays());
            r.setLopDays(0);
            r.setStatus("DRAFT");

            // use salary structure if available and requested
            if (Boolean.TRUE.equals(req.getUseSalaryStructure())) {
                structRepo.findByEmployee_IdAndIsActiveTrue(emp.getId()).ifPresent(st -> {
                    r.setBasicSalary(st.getBasicSalary());
                    r.setHra(st.getHra());
                    r.setTravelAllow(st.getTravelAllow());
                    r.setMedicalAllow(st.getMedicalAllow());
                    r.setSpecialAllow(st.getSpecialAllow());
                    r.setProvidentFund(st.getProvidentFund());
                    r.setProfessionalTax(st.getProfessionalTax());
                    r.setIncomeTax(st.getIncomeTax());
                });
            }

            // apply defaults for fields still zero
            if (r.getBasicSalary() == 0) r.setBasicSalary(safe(req.getDefaultBasic()));
            if (r.getHra()          == 0) r.setHra(safe(req.getDefaultHra()));
            if (r.getTravelAllow()  == 0) r.setTravelAllow(safe(req.getDefaultTravelAllow()));
            if (r.getMedicalAllow() == 0) r.setMedicalAllow(safe(req.getDefaultMedicalAllow()));
            if (r.getSpecialAllow() == 0) r.setSpecialAllow(safe(req.getDefaultSpecialAllow()));
            if (r.getProvidentFund()== 0) r.setProvidentFund(safe(req.getDefaultPF()));
            if (r.getProfessionalTax()==0) r.setProfessionalTax(safe(req.getDefaultPT()));

            payrollRepo.save(r);
            created++;
        }
        return created + " records created, " + skipped + " already existed for " + label;
    }

    private double safe(Double v) { return v != null ? v : 0.0; }

    private String formatMonth(String ym) {
        try { return LocalDate.parse(ym + "-01").format(DateTimeFormatter.ofPattern("MMMM yyyy")); }
        catch (Exception e) { return ym; }
    }
}