package com.bookticket.theater_service.dto;

import com.bookticket.theater_service.enums.SeatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateSeatTemplateRequest(
        @NotNull(message = "Row is required")
        @Min(value = 1, message = "Row must be greater than 0")
        Integer row,

        @NotNull(message = "Column is required")
        @Min(value = 1, message = "Column must be greater than 0")
        Integer column,

        @NotNull(message = "Seat type is required")
        SeatType seatType,

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price must be greater than or equal to 0")
        Double price
) {
}
