package com.bookticket.user_service.controller;

import com.bookticket.user_service.dto.CreateUserRequest; // Assuming this DTO has a 'roles' field
import com.bookticket.user_service.dto.UserResponse;
import com.bookticket.user_service.dto.UserSummary;
import com.bookticket.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - User Management", description = "APIs for administrative user management")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user with specific roles (Admin Only)")
    public ResponseEntity<UserSummary> createUserWithRoles(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserSummary userResponse = userService.createUserWithRoles(createUserRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }
}