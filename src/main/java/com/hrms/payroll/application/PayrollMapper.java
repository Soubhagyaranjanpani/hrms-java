// File: com/hrms/payroll/application/PayrollMapper.java
package com.hrms.payroll.application;

import com.hrms.payroll.domain.PayrollRecord;
import com.hrms.payroll.dto.PayrollRecordResponse;
import org.springframework.stereotype.Component;

@Component
public class PayrollMapper {

    public PayrollRecordResponse toResponse(PayrollRecord p) {
        PayrollRecordResponse r = new PayrollRecordResponse();
        r.setId(p.getId());
        r.setEmployeeId(p.getEmployee().getId());

        String name = clean(p.getEmployee().getFirstName())
                + " " + clean(p.getEmployee().getLastName());
        r.setEmployee(name.trim());
        r.setEmployeeCode(p.getEmployee().getEmployeeCode());
        r.setDepartment(p.getEmployee().getDepartment() != null
                ? p.getEmployee().getDepartment().getName() : "");
        r.setBranch(p.getEmployee().getBranch() != null
                ? p.getEmployee().getBranch().getName() : "");
        r.setDesignation(p.getEmployee().getRole() != null
                ? p.getEmployee().getRole().getName() : "");

        r.setYearMonth(p.getYearMonth());
        r.setPayrollMonth(p.getPayrollMonth());

        // Earnings
        r.setBasicSalary(p.getBasicSalary());
        r.setHra(p.getHra());
        r.setTravelAllow(p.getTravelAllow());
        r.setMedicalAllow(p.getMedicalAllow());
        r.setSpecialAllow(p.getSpecialAllow());
        r.setOtherEarnings(p.getOtherEarnings());
        r.setGrossEarnings(p.getGrossEarnings());

        // Deductions
        r.setProvidentFund(p.getProvidentFund());
        r.setProfessionalTax(p.getProfessionalTax());
        r.setIncomeTax(p.getIncomeTax());
        r.setLoanDeduction(p.getLoanDeduction());
        r.setOtherDeductions(p.getOtherDeductions());
        r.setTotalDeductions(p.getTotalDeductions());

        r.setNetSalary(p.getNetSalary());
        r.setWorkingDays(p.getWorkingDays());
        r.setPaidDays(p.getPaidDays());
        r.setLopDays(p.getLopDays());

        r.setStatus(p.getStatus());
        r.setPaymentDate(p.getPaymentDate());
        r.setRemarks(p.getRemarks());
        r.setAiInsight(p.getAiInsight());
        r.setProcessedBy(p.getProcessedBy());

        // Employee Info (from Employee entity)
        r.setBankAccount(p.getEmployee().getBankAccount());
        r.setUan(p.getEmployee().getUan());
        r.setPan(p.getEmployee().getPan());
        r.setDateOfJoining(p.getEmployee().getJoiningDate());

        // Additional Earnings
        r.setOvertimePayout(p.getOvertimePayout());
        r.setArrears(p.getArrears());
        r.setNoticePeriodPay(p.getNoticePeriodPay());
        r.setLeaveTravelAllowance(p.getLeaveTravelAllowance());
        r.setTelephoneAllowance(p.getTelephoneAllowance());
        r.setBonusAmount(p.getBonusAmount());
        r.setDearnessAllowance(p.getDearnessAllowance());
        r.setGradePay(p.getGradePay());
        r.setLeaveEncashment(p.getLeaveEncashment());

        // Employer Contributions
        r.setEmployerPF(p.getEmployerPF());
        r.setNpsEmployee(p.getNpsEmployee());
        r.setNpsEmployer(p.getNpsEmployer());
        r.setEsiEmployee(p.getEsiEmployee());
        r.setEsiEmployer(p.getEsiEmployer());
        r.setGratuityAccrual(p.getGratuityAccrual());
        r.setHealthEduCess(p.getHealthEduCess());

        return r;
    }

    private String clean(String s) {
        return s == null ? "" : s.replace("null", "").trim();
    }
}