package com.bookticket.theater_service.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateShowRequest(
        @NotNull @Future
        LocalDateTime startTime,
        @NotNull @Future
        LocalDateTime endTime,
        @NotNull
        String movieId
) {
}
