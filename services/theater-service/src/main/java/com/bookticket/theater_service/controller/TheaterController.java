package com.bookticket.theater_service.controller;

import com.bookticket.theater_service.dto.CreateTheaterRequest;
import com.bookticket.theater_service.dto.TheaterResponse;
import com.bookticket.theater_service.dto.UpdateTheaterRequest;
import com.bookticket.theater_service.service.TheaterService;
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
@RequestMapping("/api/v1/theaters")
@Tag(name = "Theater Controller", description = "APIs for managing theaters")
@SecurityRequirement(name = "bearerAuth")
public class TheaterController {
    private final TheaterService theaterService;

    public TheaterController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @Operation(
            summary = "Create a new theater",
            description = "Allows an ADMIN or THEATER_OWNER to add a new theater.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Theater created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TheaterResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid theater data provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges"),
                    @ApiResponse(responseCode = "409", description = "Theater already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @PostMapping
    public ResponseEntity<TheaterResponse> createTheater(@Valid @RequestBody CreateTheaterRequest createTheaterRequest) {
       return new ResponseEntity<>(theaterService.createTheater(createTheaterRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update a theater",
            description = "Allows an ADMIN or the THEATER_OWNER to update an existing theater's details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Theater updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TheaterResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid theater data provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges"),
                    @ApiResponse(responseCode = "404", description = "Theater not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @PatchMapping("/{theaterId}")
    public ResponseEntity<TheaterResponse> updateTheaterById(
            @Parameter(description = "ID of the theater to update", required = true) @PathVariable Long theaterId,
            @Valid @RequestBody UpdateTheaterRequest updateTheaterRequest) {
        return new ResponseEntity<>(theaterService.updateTheater(theaterId, updateTheaterRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Get all theaters",
            description = "Retrieves a list of all theaters. Can be optionally filtered by city.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved theaters",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TheaterResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @GetMapping
    public ResponseEntity<List<TheaterResponse>> getAllTheaters(
            @Parameter(description = "Optional city name to filter theaters") @RequestParam(required = false) String city) {
        return new ResponseEntity<>(theaterService.getAllTheaters(city), HttpStatus.OK);
    }

    @Operation(
            summary = "Get a theater by ID",
            description = "Retrieves the details of a specific theater by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Theater found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TheaterResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Theater not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @GetMapping("/{theaterId}")
    public ResponseEntity<TheaterResponse> getTheaterById(
            @Parameter(description = "ID of the theater to retrieve", required = true) @PathVariable Long theaterId) {
        return new ResponseEntity<>(theaterService.getTheater(theaterId), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a theater",
            description = "Allows an ADMIN or the THEATER_OWNER to delete a theater.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Theater deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges"),
                    @ApiResponse(responseCode = "404", description = "Theater not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @DeleteMapping("/{theaterId}")
    public ResponseEntity<Void> deleteTheaterById(
            @Parameter(description = "ID of the theater to delete", required = true) @PathVariable Long theaterId) {
        theaterService.deleteTheater(theaterId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
