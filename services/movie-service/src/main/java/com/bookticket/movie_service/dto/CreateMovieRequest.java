package com.bookticket.movie_service.dto;

import com.bookticket.movie_service.enums.Certificate;
import com.bookticket.movie_service.enums.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public record CreateMovieRequest(
        @NotBlank(message = "Movie name is required")
        String name,

        @NotBlank(message = "Movie description is required")
        String description,

        @NotBlank(message = "Movie image is required")
        String image,

        String trailer, // Trailer can be optional

        @NotEmpty(message = "Cast cannot be empty")
        List<String> cast,

        @NotEmpty(message = "Genre cannot be empty")
        Set<Genre> genre,

        @NotEmpty(message = "Language cannot be empty")
        List<String> language,

        @NotEmpty(message = "Certificate cannot be empty")
        Set<Certificate> certificate,

        Duration duration
) {
}
