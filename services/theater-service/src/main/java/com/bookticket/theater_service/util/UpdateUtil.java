package com.bookticket.theater_service.util;

import java.util.function.Consumer;

/**
 * Utility class for handling conditional updates in service layer.
 * Provides methods to update entity fields only when new values are valid.
 */
public class UpdateUtil {
    public static void updateIfNotEmpty(String newValue, Consumer<String> setter) {
        if (newValue != null && !newValue.isEmpty()) {
            setter.accept(newValue);
        }
    }

    public static <T> void updateIfNotNull(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }

    public static void updateIfNotBlank(String newValue, Consumer<String> setter) {
        if (newValue != null && !newValue.isBlank()) {
            setter.accept(newValue);
        }
    }

    public static void updateIfPositive(Integer newValue, Consumer<Integer> setter) {
        if (newValue != null && newValue > 0) {
            setter.accept(newValue);
        }
    }

    public static void updateIfPositive(Double newValue, Consumer<Double> setter) {
        if (newValue != null && newValue > 0) {
            setter.accept(newValue);
        }
    }
}

