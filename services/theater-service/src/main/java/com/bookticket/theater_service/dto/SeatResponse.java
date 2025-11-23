package com.bookticket.theater_service.dto;


public record SeatResponse(
        Long seatId,
        int row,
        int column,
        String seatType
) {
}
