package com.hrms.training.application;

import com.hrms.training.domain.TrainingRecord;
import com.hrms.training.dto.TrainingRecordResponse;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    public TrainingRecordResponse toResponse(TrainingRecord t) {
        TrainingRecordResponse r = new TrainingRecordResponse();
        r.setId(t.getId());
        r.setEmployeeId(t.getEmployee().getId());

        r.setEmployee(t.getEmployee().getFullName());
        r.setEmployeeCode(t.getEmployee().getEmployeeCode());

        r.setDepartment(t.getDepartmentName() != null ? t.getDepartmentName() : "—");
        r.setDesignation(t.getDesignationName() != null ? t.getDesignationName() : "—");

        r.setTrainingName(t.getTrainingName());
        r.setProvider(t.getProvider());

        r.setStartDate(t.getStartDate());
        r.setEndDate(t.getEndDate());

        r.setHours(t.getHours());
        r.setCertification(t.getCertification());

        r.setIsActive(t.getIsActive());

        r.setDocumentPath(t.getDocumentPath());
        r.setDocumentName(t.getDocumentName());

        return r;
    }
}
