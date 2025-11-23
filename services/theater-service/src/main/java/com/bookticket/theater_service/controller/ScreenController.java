package com.bookticket.theater_service.controller;

import com.bookticket.theater_service.dto.CreateScreenRequest;
import com.bookticket.theater_service.dto.ScreenResponse;
import com.bookticket.theater_service.dto.UpdateScreenRequest;
import com.bookticket.theater_service.service.ScreenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Screen Controller", description = "APIs for managing screens within a theater")
@SecurityRequirement(name = "bearerAuth")
public class ScreenController {

    private final ScreenService screenService;

    public ScreenController(ScreenService screenService) {
        this.screenService = screenService;
    }

    @Operation(
            summary = "Create a new screen",
            description = "Adds a new screen to a specific theater.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Screen created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScreenResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid screen data provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges"),
                    @ApiResponse(responseCode = "404", description = "Theater not found"),
                    @ApiResponse(responseCode = "409", description = "Screen already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @PostMapping("/theaters/{theaterId}/screens")
    public ResponseEntity<ScreenResponse> createScreen(
            @Parameter(description = "ID of the theater to add the screen to", required = true) @PathVariable Long theaterId,
            @Valid @RequestBody CreateScreenRequest createScreenRequest) {
        return new ResponseEntity<>(screenService.createScreen(theaterId, createScreenRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all screens for a theater",
            description = "Retrieves a list of all screens belonging to a specific theater.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved screens",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScreenResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Theater not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @GetMapping("/theaters/{theaterId}/screens")
    public ResponseEntity<List<ScreenResponse>> getScreensByTheaterId(
            @Parameter(description = "ID of the theater to retrieve screens from", required = true) @PathVariable Long theaterId) {
        return new ResponseEntity<>(screenService.getScreensByTheaterId(theaterId), HttpStatus.OK);
    }

    @Operation(
            summary = "Get a screen by ID",
            description = "Retrieves the details of a single screen by its unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Screen found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScreenResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Screen not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @GetMapping("/screens/{screenId}")
    public ResponseEntity<ScreenResponse> getScreenById(
            @Parameter(description = "ID of the screen to retrieve", required = true) @PathVariable Long screenId) {
        return new ResponseEntity<>(screenService.getScreenById(screenId), HttpStatus.OK);
    }

    @Operation(
            summary = "Update a screen",
            description = "Updates an existing screen's details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Screen updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScreenResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid screen data provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges"),
                    @ApiResponse(responseCode = "404", description = "Screen not found")
            }
    )
    @PatchMapping("/screens/{screenId}")
    public ResponseEntity<ScreenResponse> updateScreenById(
            @Parameter(description = "ID of the screen to update", required = true) @PathVariable Long screenId,
            @Valid @RequestBody UpdateScreenRequest updateScreenRequest) {
        return new ResponseEntity<>(screenService.updateScreen(screenId, updateScreenRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a screen",
            description = "Deletes a specific screen.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Screen deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges"),
                    @ApiResponse(responseCode = "404", description = "Screen not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @DeleteMapping("/screens/{screenId}")
    public ResponseEntity<Void> deleteScreenById(
            @Parameter(description = "ID of the screen to delete", required = true) @PathVariable Long screenId) {
        screenService.deleteScreen(screenId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
