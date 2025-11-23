package com.bookticket.theater_service.dto;

import jakarta.validation.constraints.NotNull;

public record CreateTheaterRequest(
        @NotNull(message = "Name is required")
        String name,
        @NotNull(message = "Address is required")
        String address,
        @NotNull(message = "City is required")
        String city,
        @NotNull(message = "State is required")
        String state,
        String zip,
        String landmark
) {
}
