package com.bookticket.user_service.dto;

public record UpdateUserRequest(
        String username,
        String email
) {
}
