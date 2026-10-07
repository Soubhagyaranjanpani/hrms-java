package com.hrms.payroll.application;



import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.payroll.dto.PayrollRecordResponse;
import com.hrms.payroll.dto.UpdatePayrollRequest;
import com.hrms.payroll.infrastructure.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatePayrollRecordUseCase {

    private final PayrollRepository repo;
    private final PayrollMapper     mapper;

    public PayrollRecordResponse execute(Long id, UpdatePayrollRequest req) {
        PayrollRecord p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll record not found"));

        if (!"DRAFT".equals(p.getStatus()) && !"PENDING".equals(p.getStatus()))
            throw new RuntimeException("Cannot edit record with status: " + p.getStatus());

        if (req.getBasicSalary()      != null) p.setBasicSalary(req.getBasicSalary());
        if (req.getHra()              != null) p.setHra(req.getHra());
        if (req.getTravelAllow()      != null) p.setTravelAllow(req.getTravelAllow());
        if (req.getMedicalAllow()     != null) p.setMedicalAllow(req.getMedicalAllow());
        if (req.getSpecialAllow()     != null) p.setSpecialAllow(req.getSpecialAllow());
        if (req.getOtherEarnings()    != null) p.setOtherEarnings(req.getOtherEarnings());
        if (req.getProvidentFund()    != null) p.setProvidentFund(req.getProvidentFund());
        if (req.getProfessionalTax()  != null) p.setProfessionalTax(req.getProfessionalTax());
        if (req.getIncomeTax()        != null) p.setIncomeTax(req.getIncomeTax());
        if (req.getLoanDeduction()    != null) p.setLoanDeduction(req.getLoanDeduction());
        if (req.getOtherDeductions()  != null) p.setOtherDeductions(req.getOtherDeductions());
        if (req.getWorkingDays()      != null) p.setWorkingDays(req.getWorkingDays());
        if (req.getPaidDays()         != null) p.setPaidDays(req.getPaidDays());
        if (req.getLopDays()          != null) p.setLopDays(req.getLopDays());
        if (req.getRemarks()          != null) p.setRemarks(req.getRemarks());
        p.setStatus("PENDING"); // editing moves it to PENDING for approval

        return mapper.toResponse(repo.save(p));
    }
}
