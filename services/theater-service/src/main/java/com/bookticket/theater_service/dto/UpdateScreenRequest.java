package com.bookticket.theater_service.dto;

import com.bookticket.theater_service.enums.ScreenType;

public record UpdateScreenRequest(
        String name,
        int rows,
        int columns,
        ScreenType screenType
) {
}
