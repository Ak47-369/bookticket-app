package com.bookticket.user_service.dto;

import lombok.Builder;

@Builder
public record LoginResponse(
        UserSummary userSummary,
        JwtResponse jwtResponse
) {
}
