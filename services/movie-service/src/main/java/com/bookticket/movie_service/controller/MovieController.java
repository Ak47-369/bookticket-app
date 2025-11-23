package com.bookticket.movie_service.controller;

import com.bookticket.movie_service.dto.CreateMovieRequest;
import com.bookticket.movie_service.dto.MovieResponse;
import com.bookticket.movie_service.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/movies")
@Tag(name = "Movie Controller", description = "APIs for managing movie information")
@SecurityRequirement(name = "bearerAuth") // Apply security globally to all endpoints in this controller
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(
            summary = "Create a new movie",
            description = "Allows ADMIN or THEATER_OWNER to add a new movie to the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movie created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid movie data provided",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN or THEATER_OWNER role",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "503", description = "Service unavailable",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "429", description = "Too many requests",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "409", description = "Conflict - Movie already exists",
                            content = @Content(mediaType = "application/json"))
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATER_OWNER')")
    public ResponseEntity<MovieResponse> createMovie(@RequestBody CreateMovieRequest createMovieRequest) {
        return ResponseEntity.ok(movieService.createMovie(createMovieRequest));
    }

    @Operation(
            summary = "Get all movies",
            description = "Retrieves a list of all movies available in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of movies",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "503", description = "Service unavailable",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "429", description = "Too many requests",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'THEATER_OWNER')")
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @Operation(
            summary = "Get movie by ID",
            description = "Retrieves details of a specific movie by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movie found and retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "Movie not found",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "503", description = "Service unavailable",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "429", description = "Too many requests",
                            content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'THEATER_OWNER')")
    public ResponseEntity<MovieResponse> getMovieById(
            @Parameter(description = "ID of the movie to retrieve", required = true)
            @PathVariable String id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @Operation(
            summary = "Get movies by city",
            description = "Retrieves a list of movies currently playing in a specific city.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of movies for the city",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid city parameter provided",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "503", description = "Service unavailable",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "504", description = "Gateway timeout",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "429", description = "Too many requests",
                            content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping("/now-playing")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'THEATER_OWNER')")
    public ResponseEntity<List<MovieResponse>> getMoviesByCity(
            @Parameter(description = "Name of the city to filter movies by", required = true)
            @RequestParam String city) {
        return ResponseEntity.ok(movieService.findMoviesByCity(city));
    }
}
