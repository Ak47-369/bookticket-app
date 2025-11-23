package com.bookticket.theater_service.controller;

import com.bookticket.theater_service.configuration.swagger.InternalApi;
import com.bookticket.theater_service.dto.*;
import com.bookticket.theater_service.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@Tag(name = "Seat Controller", description = "APIs for managing seat templates and availability")
@SecurityRequirement(name = "bearerAuth")
public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @Operation(
            summary = "Create a seat template for a screen",
            description = "Defines the layout, numbering, and pricing for all seats in a specific screen.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Seat template created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SeatTemplateResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Screen not found")
            }
    )
    @PostMapping("/screens/{screenId}/seats")
    public ResponseEntity<List<SeatTemplateResponse>> createSeatTemplate(
            @Parameter(description = "ID of the screen to create the template for", required = true) @PathVariable Long screenId,
            @Valid @RequestBody List<CreateSeatTemplateRequest> createSeatTemplateRequests) {
        return new ResponseEntity<>(seatService.createSeatTemplate(screenId, createSeatTemplateRequests), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get the seat template for a screen",
            description = "Retrieves the defined seat layout for a specific screen.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved seat template",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SeatTemplateResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Screen not found")
            }
    )
    @GetMapping("/screens/{screenId}/seats")
    public ResponseEntity<List<SeatTemplateResponse>> getSeatTemplateByScreenId(
            @Parameter(description = "ID of the screen to get the template for", required = true) @PathVariable Long screenId) {
        return new ResponseEntity<>(seatService.getSeatTemplateByScreenId(screenId), HttpStatus.OK);
    }

    @InternalApi
    @Operation(summary = "Verify seat availability")
    @PostMapping("/shows/internal/seats/verify")
    public ResponseEntity<List<ValidSeatResponse>> verifySeats(@Valid @RequestBody VerfiySeatRequest verifySeatRequest) {
        List<ValidSeatResponse> validSeatResponses = seatService.getAvailableSeats(verifySeatRequest.showId(), verifySeatRequest.seatIds());
        if(validSeatResponses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.info("Validated {} seats", validSeatResponses.toString());
        if(validSeatResponses.size() != verifySeatRequest.seatIds().size()) {
            return new ResponseEntity<>(validSeatResponses,HttpStatus.PARTIAL_CONTENT);
        }
        return new ResponseEntity<>(validSeatResponses, HttpStatus.OK);
    }

    @InternalApi
    @Operation(summary = "Lock seats for a show")
    @PostMapping("/shows/internal/seats/lock")
    public ResponseEntity<List<ValidSeatResponse>> lockSeats(@Valid @RequestBody LockSeatsRequest lockSeatsRequest) {
        return new ResponseEntity<>(seatService.lockSeatsByShowAndSeatIds(lockSeatsRequest), HttpStatus.OK);
    }

    @InternalApi
    @Operation(summary = "Release locked seats for a show")
    @PostMapping("/shows/internal/seats/release")
    public ResponseEntity<List<ValidSeatResponse>> releaseSeats(@Valid @RequestBody ReleaseSeatsRequest releaseSeatsRequest) {
        return new ResponseEntity<>(seatService.releaseSeatsByShowAndSeatIds(releaseSeatsRequest), HttpStatus.OK);
    }

    @InternalApi
    @Operation(summary = "Book seats for a show")
    @PostMapping("/shows/internal/seats/book")
    public ResponseEntity<List<ValidSeatResponse>> bookSeats(@Valid @RequestBody BookSeatsRequest bookSeatsRequest) {
        return new ResponseEntity<>(seatService.bookSeatsByShowAndSeatIds(bookSeatsRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Update a single seat's price",
            description = "Updates the price of an individual seat within a screen's template.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Seat price updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SeatTemplateResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Screen or Seat not found")
            }
    )
    @PatchMapping("/screens/{screenId}/seats/{seatId}/price")
    public ResponseEntity<SeatTemplateResponse> updateSeatPrice(
            @Parameter(description = "ID of the screen", required = true) @PathVariable Long screenId,
            @Parameter(description = "ID of the seat to update", required = true) @PathVariable Long seatId,
            @Parameter(description = "The new price for the seat", required = true) @RequestParam Double price) {
        return new ResponseEntity<>(seatService.updateSeatPrice(screenId, seatId, price), HttpStatus.OK);
    }

    @Operation(
            summary = "Update a single seat's type",
            description = "Updates the type (e.g., 'PREMIUM', 'STANDARD') of an individual seat within a screen's template.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Seat type updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SeatTemplateResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Screen or Seat not found")
            }
    )
    @PatchMapping("/screens/{screenId}/seats/{seatId}/type")
    public ResponseEntity<SeatTemplateResponse> updateSeatType(
            @Parameter(description = "ID of the screen", required = true) @PathVariable Long screenId,
            @Parameter(description = "ID of the seat to update", required = true) @PathVariable Long seatId,
            @Parameter(description = "The new type for the seat", required = true) @RequestParam String seatType) {
        return new ResponseEntity<>(seatService.updateSeatType(screenId, seatId, seatType), HttpStatus.OK);
    }

    @Operation(
            summary = "Update an entire seat template",
            description = "Replaces the entire seat template for a screen. Use with caution.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Seat template updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SeatTemplateResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Screen not found")
            }
    )
    @PutMapping("/screens/{screenId}/seats")
    public ResponseEntity<List<SeatTemplateResponse>> updateSeatTemplate(
            @Parameter(description = "ID of the screen to update the template for", required = true) @PathVariable Long screenId,
            @Valid @RequestBody List<CreateSeatTemplateRequest> createSeatTemplateRequests) {
        return new ResponseEntity<>(seatService.updateSeatTemplate(screenId, createSeatTemplateRequests), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a seat",
            description = "Deletes a single seat from a screen's template.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Seat deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Screen or Seat not found")
            }
    )
    @DeleteMapping("/screens/{screenId}/seats/{seatId}")
    public ResponseEntity<Void> deleteSeat(
            @Parameter(description = "ID of the screen", required = true) @PathVariable Long screenId,
            @Parameter(description = "ID of the seat to delete", required = true) @PathVariable Long seatId) {
        seatService.deleteSeat(screenId, seatId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
