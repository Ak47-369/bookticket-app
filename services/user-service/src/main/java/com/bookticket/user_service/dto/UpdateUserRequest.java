package com.bookticket.user_service.dto;

public record UpdateUserRequest(
        String username,
        String email
        // TODO - Add password update [Also take current password and update iff matches]
) {
}
