package com.bookticket.user_service.controller;

import com.bookticket.user_service.dto.CreateUserRequest;
import com.bookticket.user_service.dto.UserSummary;
import com.bookticket.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @Operation(
            summary = "Create a new user with specific roles",
            description = "Allows administrators to create new users with specified roles. " +
                    "This endpoint is protected and requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateUserRequest.class),
                            examples = @ExampleObject(
                                    value = """
                {
                    "username": "admin_user",
                    "email": "admin@example.com",
                    "password": "securePassword123",
                    "roles": ["ADMIN", "USER"]
                }"""
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserSummary.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data (e.g., email already exists, invalid role)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Only administrators can access this endpoint",
                    content = @Content
            )
    })

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserSummary> createUserWithRoles(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserSummary userResponse = userService.createUserWithRoles(createUserRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }
}