// File: com/hrms/payroll/application/BulkGeneratePayrollUseCase.java
package com.hrms.payroll.application;

import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.payroll.dto.BulkGenerateRequest;
import com.hrms.payroll.infrastructure.PayrollRepository;
import com.hrms.payroll.infrastructure.SalaryStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BulkGeneratePayrollUseCase {

    private final PayrollRepository payrollRepo;
    private final EmployeeRepository empRepo;
    private final SalaryStructureRepository structRepo;
    private final SalaryConfigService configService;

    public String execute(BulkGenerateRequest req) {
        List<Employee> employees = empRepo.findByIsActiveTrueAndIsDeletedFalse();
        String ym = req.getYearMonth();
        String label = formatMonth(ym);
        int created = 0, skipped = 0;

        Map<String, Double> cfg = configService.getAllConfigValues();

        for (Employee emp : employees) {
            if (payrollRepo.findByEmployee_IdAndYearMonthAndIsDeletedFalse(emp.getId(), ym).isPresent()) {
                skipped++;
                continue;
            }
            PayrollRecord r = new PayrollRecord();
            r.setEmployee(emp);
            r.setYearMonth(ym);
            r.setPayrollMonth(label);
            r.setWorkingDays(req.getWorkingDays() != null ? req.getWorkingDays() : 26);
            r.setPaidDays(r.getWorkingDays());
            r.setLopDays(0);
            r.setStatus("DRAFT");

            // Use salary structure if available
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
                    r.setDearnessAllowance(st.getDearnessAllowance());
                    r.setGradePay(st.getGradePay());
                    r.setBonusAmount(st.getBonusAmount());
                    r.setLeaveEncashment(st.getLeaveEncashment());
                    r.setEmployerPF(st.getEmployerPF());
                    r.setNpsEmployee(st.getNpsEmployee());
                    r.setNpsEmployer(st.getNpsEmployer());
                    r.setEsiEmployee(st.getEsiEmployee());
                    r.setEsiEmployer(st.getEsiEmployer());
                    r.setGratuityAccrual(st.getGratuityAccrual());
                    r.setHealthEduCess(st.getHealthEduCess());
                });
            }

            // Fallback to request defaults (these come from frontend, not hardcoded)
            if (r.getBasicSalary() == 0 && req.getDefaultBasic() != null) r.setBasicSalary(req.getDefaultBasic());
            if (r.getHra() == 0 && req.getDefaultHra() != null) r.setHra(req.getDefaultHra());
            if (r.getTravelAllow() == 0) r.setTravelAllow(req.getDefaultTravelAllow() != null ? req.getDefaultTravelAllow() : (cfg.get("TRAVEL_ALLOW_MIN") != null ? cfg.get("TRAVEL_ALLOW_MIN") : 0));
            if (r.getMedicalAllow() == 0) r.setMedicalAllow(req.getDefaultMedicalAllow() != null ? req.getDefaultMedicalAllow() : (cfg.get("MEDICAL_ALLOW_MIN") != null ? cfg.get("MEDICAL_ALLOW_MIN") : 0));
            if (r.getSpecialAllow() == 0 && req.getDefaultSpecialAllow() != null) r.setSpecialAllow(req.getDefaultSpecialAllow());
            if (r.getProvidentFund() == 0 && req.getDefaultPF() != null) r.setProvidentFund(req.getDefaultPF());
            if (r.getProfessionalTax() == 0) r.setProfessionalTax(req.getDefaultPT() != null ? req.getDefaultPT() : (cfg.get("PROF_TAX_MAX") != null ? cfg.get("PROF_TAX_MAX") : 0));

            r.compute();
            payrollRepo.save(r);
            created++;
        }
        return created + " records created, " + skipped + " already existed for " + label;
    }

    private String formatMonth(String ym) {
        try {
            return LocalDate.parse(ym + "-01").format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        } catch (Exception e) {
            return ym;
        }
    }
}