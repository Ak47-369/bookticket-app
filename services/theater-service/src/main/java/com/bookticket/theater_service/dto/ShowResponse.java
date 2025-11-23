package com.bookticket.theater_service.dto;

import java.time.LocalDateTime;

public record ShowResponse(
        Long showId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String movieId,
        String movieTitle,
        String movieBannerUrl,
        String theaterName,
        String theaterAddress,
        String screenName
) {
}
