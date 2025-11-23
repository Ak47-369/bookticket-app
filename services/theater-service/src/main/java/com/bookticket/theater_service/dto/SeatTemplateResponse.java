package com.bookticket.theater_service.dto;

public record SeatTemplateResponse(
        String seatNumber,
        String seatType,
        double price
) {
}
