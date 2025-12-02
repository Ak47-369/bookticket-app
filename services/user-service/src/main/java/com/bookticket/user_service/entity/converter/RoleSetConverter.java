package com.bookticket.user_service.entity.converter;

import com.bookticket.user_service.enums.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This converter allows a Set of UserRole enums to be stored as a single
 * comma-separated string in a database column.
 */
@Converter
public class RoleSetConverter implements AttributeConverter<Set<UserRole>, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(Set<UserRole> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        // Convert the Set of enums to a comma-separated string of their names
        return attribute.stream()
                .map(UserRole::name)
                .collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public Set<UserRole> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(dbData.split(SEPARATOR))
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());
    }
}