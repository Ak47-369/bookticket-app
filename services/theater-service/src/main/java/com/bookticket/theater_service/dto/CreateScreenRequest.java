package com.bookticket.theater_service.dto;

import com.bookticket.theater_service.enums.ScreenType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateScreenRequest(
        @NotNull(message = "Screen Name is required")
        String name,

        @NotNull(message = "Rows are required")
        @Min(1)
        int rows,
        @NotNull(message = "Columns are required")
        @Min(1)
        int columns,
        ScreenType screenType
) {
}
