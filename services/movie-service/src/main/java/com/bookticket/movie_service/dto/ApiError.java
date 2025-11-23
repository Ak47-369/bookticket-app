package com.bookticket.movie_service.dto;

import java.time.LocalDateTime;

public record ApiError(
        LocalDateTime timestamp,
        String message,
        String details
) {
    public ApiError(String message, String details) {
        this(LocalDateTime.now(), message, details);
    }
}
