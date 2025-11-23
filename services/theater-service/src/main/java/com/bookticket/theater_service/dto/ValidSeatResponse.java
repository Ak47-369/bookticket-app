package com.bookticket.theater_service.dto;

public record ValidSeatResponse(
        Long seatId,
        String seatNumber,
        String seatType,
        Double seatPrice
) {
}
