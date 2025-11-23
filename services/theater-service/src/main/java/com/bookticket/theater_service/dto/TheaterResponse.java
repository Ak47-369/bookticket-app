package com.bookticket.theater_service.dto;

public record TheaterResponse(
        Long theaterId,
        String name,
        String address
) {
}
