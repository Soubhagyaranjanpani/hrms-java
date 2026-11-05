package com.hrms.loan_advance.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LoanStatusConverter implements AttributeConverter<LoanStatus, String> {

    @Override
    public String convertToDatabaseColumn(LoanStatus attribute) {
        return attribute == null ? null : attribute.getLabel();
    }

    @Override
    public LoanStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : LoanStatus.fromLabel(dbData);
    }
}
