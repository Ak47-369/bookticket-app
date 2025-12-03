package com.bookticket.user_service.controller;

import com.bookticket.user_service.dto.*;
import com.bookticket.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody as OpenApiRequestBody;
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
            description = "Retrieves the profile of the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserSummary.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication token is missing or invalid", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UserSummary> getCurrentUserProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @Operation(
            summary = "Change current user's password",
            description = "Allows an authenticated user to change their own password. Requires the current password for verification.",
            requestBody = @OpenApiRequestBody(
                    description = "The user's current and new password details.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdatePasswordRequest.class),
                            examples = @ExampleObject(
                                    name = "Password Change Request",
                                    summary = "Example for changing a password",
                                    value = "{\"currentPassword\": \"oldPassword123\", \"newPassword\": \"newSecurePassword456\", \"confirmNewPassword\": \"newSecurePassword456\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., current password incorrect, passwords don't match)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content)
    })
    @PostMapping("/me/change-password")
    public ResponseEntity<String> changePassword(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest
    ) {
        userService.updatePasswordByEmail(userDetails.getUsername(), updatePasswordRequest);
        return ResponseEntity.ok("Password changed successfully");
    }

    @Operation(
            summary = "Update current user profile",
            description = "Updates the profile information (e.g., username) of the currently authenticated user.",
            requestBody = @OpenApiRequestBody(
                    description = "The updated user profile information.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateUserRequest.class),
                            examples = @ExampleObject(
                                    name = "Profile Update Request",
                                    summary = "Example for updating a user's profile",
                                    value = "{\"username\": \"Johnathan Doe\", \"email\": \"john.doe.new@example.com\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already in use by another account", content = @Content)
    })
    @PutMapping("/me")
    public ResponseEntity<?> updateUserProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        String email = userDetails.getUsername();
        LoginResponse loginResponse = userService.updateUserByEmail(email, updateUserRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(
            summary = "Delete current user account",
            description = "Permanently deletes the account of the currently authenticated user. This action is irreversible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User account deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        userService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get user email by ID (Internal)",
            description = "Retrieves the email address of a user by their ID. Intended for internal service-to-service communication.",
            hidden = true
    )
    @GetMapping("/{userId}/email")
    public ResponseEntity<String> getEmailById(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(userService.getUserById(userId).getEmail());
    }
}
