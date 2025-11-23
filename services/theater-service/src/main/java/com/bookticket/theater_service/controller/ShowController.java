package com.bookticket.theater_service.controller;

import com.bookticket.theater_service.dto.CreateShowRequest;
import com.bookticket.theater_service.dto.ShowResponse;
import com.bookticket.theater_service.dto.ShowSeatResponse;
import com.bookticket.theater_service.service.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shows")
@Tag(name = "Show Controller", description = "APIs for managing movie shows and seat availability")
@SecurityRequirement(name = "bearerAuth")
public class ShowController {
    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @Operation(
            summary = "Create a new show",
            description = "Schedules a new show for a specific screen.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Show created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShowResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid show data provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges"),
                    @ApiResponse(responseCode = "404", description = "Screen not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @PostMapping("/screens/{screenId}")
    public ResponseEntity<ShowResponse> createShow(
            @Parameter(description = "ID of the screen to schedule the show on", required = true) @PathVariable Long screenId,
            @Valid @RequestBody CreateShowRequest createShowRequest) {
        return new ResponseEntity<>(showService.createShow(screenId, createShowRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Find shows with filters",
            description = "Finds shows with flexible filtering. Supports combinations of city, theaterId, movieId, and date. If no date is provided, returns shows for the next 15 days.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved shows",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShowResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid filter parameters provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @GetMapping
    public ResponseEntity<List<ShowResponse>> getShows(
            @Parameter(description = "Filter by city name") @RequestParam(required = false) String city,
            @Parameter(description = "Filter by theater ID") @RequestParam(required = false) Long theaterId,
            @Parameter(description = "Filter by movie ID") @RequestParam(required = false) String movieId,
            @Parameter(description = "Filter by specific date (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ShowResponse> showResponses = showService.getShowsByFilters(city, theaterId, movieId, date);
        return ResponseEntity.ok(showResponses);
    }

    @Operation(
            summary = "Get movie IDs by city (Internal)",
            description = "An internal service-to-service endpoint to get a list of movie IDs playing in a specific city. Not intended for public use.",
            hidden = true
    )
    @GetMapping("/internal/movie-ids")
    public ResponseEntity<List<String>> getMovieIdsByCity(@RequestParam String city) {
        return ResponseEntity.ok(showService.getMovieIdsByCity(city));
    }

    @Operation(
            summary = "Get shows by screen ID",
            description = "Retrieves all shows scheduled for a specific screen.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved shows",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShowResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Screen not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @GetMapping("/screens/{screenId}")
    public ResponseEntity<List<ShowResponse>> getShowsByScreenId(
            @Parameter(description = "ID of the screen to retrieve shows for", required = true) @PathVariable Long screenId) {
        return ResponseEntity.ok(showService.getShowsByScreenId(screenId));
    }

    @Operation(
            summary = "Get a show by ID",
            description = "Retrieves the details of a specific show by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Show found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShowResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Show not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @GetMapping("/{showId}")
    public ResponseEntity<ShowResponse> getShowById(
            @Parameter(description = "ID of the show to retrieve", required = true) @PathVariable Long showId) {
        return ResponseEntity.ok(showService.getShowById(showId));
    }

    @Operation(
            summary = "Get seat map for a show",
            description = "Retrieves the complete seat map for a show, including the layout and availability status of each seat.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved seat map",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShowSeatResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Show not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable"),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout"),
                    @ApiResponse(responseCode = "429", description = "Too many requests")
            }
    )
    @GetMapping("/{showId}/seats")
    public ResponseEntity<List<ShowSeatResponse>> getShowSeats(
            @Parameter(description = "ID of the show to get the seat map for", required = true) @PathVariable Long showId) {
        List<ShowSeatResponse> showSeatResponses = showService.getShowSeatsByShowId(showId);
        if(showSeatResponses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(showService.getShowSeatsByShowId(showId));
    }
}
