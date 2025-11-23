package com.bookticket.user_service.controller;

import com.bookticket.user_service.dto.*;
import com.bookticket.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing user accounts")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile of the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user profile",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserSummary.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication token is missing or invalid",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service unavailable",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "504",
                    description = "Gateway timeout",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content
            )
    })
    @GetMapping("/me")
    public ResponseEntity<UserSummary> getUserByUsername(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @Operation(
            summary = "Get user email by ID",
            description = "Retrieves the email address of a user by their ID (public endpoint)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user email",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string", example = "user@example.com")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service unavailable",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "504",
                    description = "Gateway timeout",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content
            )
    })
    @GetMapping("/{userId}/email")
    public ResponseEntity<String> getEmailById(
            @Parameter(description = "ID of the user", required = true, example = "123")
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(userService.getUserById(userId).getEmail());
    }

    @Operation(
            summary = "Update current user profile",
            description = "Updates the profile of the currently authenticated user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateUserRequest.class),
                            examples = @ExampleObject(
                                    value = "{\"name\": \"Updated Name\", \"email\": \"updated@example.com\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication token is missing or invalid",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already in use",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service unavailable",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "504",
                    description = "Gateway timeout",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content
            )
    })
    @PutMapping("/update")
    public ResponseEntity<JwtResponse> updateUserByUsername(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        String email = userDetails.getUsername();
        JwtResponse jwtResponse = userService.updateUserByEmail(email, updateUserRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @Operation(
            summary = "Delete current user account",
            description = "Permanently deletes the account of the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User account deleted successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication token is missing or invalid",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service unavailable",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "504",
                    description = "Gateway timeout",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Insufficient privileges",
                    content = @Content
            )
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUserByEmail(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        userService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }
}
