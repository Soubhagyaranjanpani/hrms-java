package com.hrms.payroll.application;



import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import com.hrms.payroll.domain.SalaryStructure;
import com.hrms.payroll.dto.SalaryStructureRequest;
import com.hrms.payroll.infrastructure.SalaryStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveSalaryStructureUseCase {

    private final SalaryStructureRepository structRepo;
    private final EmployeeRepository        empRepo;

    public String execute(SalaryStructureRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        SalaryStructure s = structRepo.findByEmployee_IdAndIsActiveTrue(emp.getId())
                .orElseGet(SalaryStructure::new);

        s.setEmployee(emp);
        s.setBasicSalary(req.getBasicSalary());
        s.setHra(req.getHra());
        s.setTravelAllow(req.getTravelAllow());
        s.setMedicalAllow(req.getMedicalAllow());
        s.setSpecialAllow(req.getSpecialAllow());
        s.setProvidentFund(req.getProvidentFund());
        s.setProfessionalTax(req.getProfessionalTax());
        s.setIncomeTax(req.getIncomeTax());
        s.setCtc(req.getCtc());
        s.setIsActive(true);

        structRepo.save(s);
        return "Salary structure saved for " + emp.getFirstName();
    }
}
