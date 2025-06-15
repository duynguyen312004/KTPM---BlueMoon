package org.example.condomanagement.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VehicleTypeConverter implements AttributeConverter<VehicleType, String> {
    @Override
    public String convertToDatabaseColumn(VehicleType attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public VehicleType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : VehicleType.valueOf(dbData);
    }
} 