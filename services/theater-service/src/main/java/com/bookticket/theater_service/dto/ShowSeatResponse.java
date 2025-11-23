package com.bookticket.theater_service.dto;

import com.bookticket.theater_service.enums.SeatType;
import com.bookticket.theater_service.enums.ShowSeatStatus;

public record ShowSeatResponse(
        Long showSeatId,
        String seatNumber,
        SeatType seatType,
        double price,
        ShowSeatStatus status
) {
}
