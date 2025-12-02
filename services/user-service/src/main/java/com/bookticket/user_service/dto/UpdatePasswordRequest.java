package com.bookticket.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 6, max = 50, message = "New password must be between 6 and 50 characters")
        String newPassword,

        @NotBlank(message = "Confirm new password is required")
        String confirmNewPassword
) {}