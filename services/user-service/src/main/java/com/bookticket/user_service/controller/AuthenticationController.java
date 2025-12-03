package com.bookticket.user_service.controller;

import com.bookticket.user_service.dto.*;
import com.bookticket.user_service.service.CustomUserDetails;
import com.bookticket.user_service.service.UserDetailsServiceImpl;
import com.bookticket.user_service.service.UserService;
import com.bookticket.user_service.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication API for user registration and login")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService,
                                    JwtUtils jwtUtils, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Operation(
            summary = "Register a new user with default role USER",
            description = "Creates a new user account with the provided details and returns a JWT token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The user's details for registration.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "Standard User Registration",
                                    summary = "Example for registering a new user",
                                    value = "{\"username\": \"johndoe\", \"email\": \"john.doe@example.com\", \"password\": \"password123\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserSummary userSummary = userService.createUser(registerRequest);
        final CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(userSummary.email());
        final String jwt = jwtUtils.generateToken(userDetails);
        final long expiresIn = jwtUtils.extractExpiration(jwt).getTime();

        return ResponseEntity.ok(new LoginResponse(userSummary, new JwtResponse(jwt, expiresIn)));
    }

    @Operation(
            summary = "Authenticate user and get a token",
            description = "Authenticates a user with their email and password and returns a JWT token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The user's credentials for login.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "User Login",
                                    summary = "Example for user authentication",
                                    value = "{\"email\": \"john.doe@example.com\", \"password\": \"password123\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        final CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(loginRequest.email());
        final String jwt = jwtUtils.generateToken(userDetails);
        final long expiresIn = jwtUtils.extractExpiration(jwt).getTime();

        return ResponseEntity.ok(new JwtResponse(jwt, expiresIn));
    }
}
