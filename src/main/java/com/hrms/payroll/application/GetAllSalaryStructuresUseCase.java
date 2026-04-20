package com.hrms.payroll.application;



import com.hrms.payroll.domain.SalaryStructure;
import com.hrms.payroll.dto.SalaryStructureResponse;
import com.hrms.payroll.infrastructure.SalaryStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllSalaryStructuresUseCase {

    private final SalaryStructureRepository repo;

    public List<SalaryStructureResponse> execute() {
        return repo.findByIsActiveTrue()
                .stream()
                .map(this::map)
                .toList();
    }

    private SalaryStructureResponse map(SalaryStructure s) {
        SalaryStructureResponse r = new SalaryStructureResponse();
        r.setId(s.getId());
        r.setEmployeeId(s.getEmployee().getId());

        String name = clean(s.getEmployee().getFirstName())
                + " " + clean(s.getEmployee().getLastName());
        r.setEmployeeName(name.trim());
        r.setEmployeeCode(s.getEmployee().getEmployeeCode());
        r.setDepartment(s.getEmployee().getDepartment() != null
                ? s.getEmployee().getDepartment().getName() : "");
        r.setBranch(s.getEmployee().getBranch() != null
                ? s.getEmployee().getBranch().getName() : "");

        r.setBasicSalary(s.getBasicSalary());
        r.setHra(s.getHra());
        r.setTravelAllow(s.getTravelAllow());
        r.setMedicalAllow(s.getMedicalAllow());
        r.setSpecialAllow(s.getSpecialAllow());
        r.setProvidentFund(s.getProvidentFund());
        r.setProfessionalTax(s.getProfessionalTax());
        r.setIncomeTax(s.getIncomeTax());
        r.setCtc(s.getCtc());
        return r;
    }

    private String clean(String s) {
        return s == null ? "" : s.replace("null","").trim();
    }
}
