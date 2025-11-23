package com.bookticket.user_service.dto;

import lombok.Builder;

@Builder
public record JwtResponse(
        String token,
        String type,
        long expiresIn
) {
    public JwtResponse(String token, long expiresIn) {
        this(token, "Bearer", expiresIn);
    }
}
