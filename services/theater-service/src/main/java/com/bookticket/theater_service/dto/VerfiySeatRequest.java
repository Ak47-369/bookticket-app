package com.bookticket.theater_service.dto;

import java.util.List;

public record VerfiySeatRequest(
        Long showId,
        List<Long> seatIds
) {
}
