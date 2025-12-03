package com.bookticket.theater_service.dto;

public record SeatTemplateResponse(
        Long seatId,
        String seatNumber,
        String seatType,
        double price
) {
}
