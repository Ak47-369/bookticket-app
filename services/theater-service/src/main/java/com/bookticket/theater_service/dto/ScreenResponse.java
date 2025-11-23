package com.bookticket.theater_service.dto;

import com.bookticket.theater_service.enums.ScreenType;

public record ScreenResponse(
        Long screenId,
        String name,
        int rows,
        int columns,
        int totalSeats,
        ScreenType screenType
) {
    public ScreenResponse(Long screenId, String name, int rows, int columns, ScreenType screenType) {
        this(screenId, name, rows, columns, rows * columns, screenType);
    }
}
