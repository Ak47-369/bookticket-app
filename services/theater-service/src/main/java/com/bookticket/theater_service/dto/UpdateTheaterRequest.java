package com.bookticket.theater_service.dto;

public record UpdateTheaterRequest(
        String name,
        String address,
        String city,
        String state,
        String zip,
        String landmark
) {
}
