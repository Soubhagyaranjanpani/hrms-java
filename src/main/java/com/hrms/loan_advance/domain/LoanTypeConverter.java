package com.hrms.loan_advance.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LoanTypeConverter implements AttributeConverter<LoanType, String> {

    @Override
    public String convertToDatabaseColumn(LoanType attribute) {
        return attribute == null ? null : attribute.getLabel();
    }

    @Override
    public LoanType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : LoanType.fromLabel(dbData);
    }
}
