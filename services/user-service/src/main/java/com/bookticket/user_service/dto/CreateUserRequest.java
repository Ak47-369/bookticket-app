package com.bookticket.user_service.dto;

import com.bookticket.user_service.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateUserRequest(
        @NotBlank
        String username,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 6, max = 50)
        String password,
        @NotEmpty(message = "Roles cannot be empty")
        List<UserRole> roles
) {}
