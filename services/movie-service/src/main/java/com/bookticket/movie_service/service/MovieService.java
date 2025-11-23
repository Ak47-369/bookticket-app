package com.bookticket.movie_service.service;

import com.bookticket.movie_service.dto.CreateMovieRequest;
import com.bookticket.movie_service.dto.MovieResponse;
import com.bookticket.movie_service.entity.Movie;
import com.bookticket.movie_service.exception.ResourceNotFoundException;
import com.bookticket.movie_service.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final RestClient theaterRestClient;

    public MovieService(MovieRepository movieRepository, RestClient theaterRestClient) {
        this.movieRepository = movieRepository;
        this.theaterRestClient = theaterRestClient;
    }

    public MovieResponse createMovie(CreateMovieRequest request) {
        log.info("Creating movie with name: {}", request.name());
        Movie movie = new Movie();
        movie.setName(request.name());
        movie.setDescription(request.description());
        movie.setImage(request.image());
        movie.setTrailer(request.trailer());
        movie.setCast(request.cast());
        movie.setGenre(request.genre().stream().toList());
        movie.setLanguage(request.language());
        movie.setCertificate(request.certificate().stream().toList());
        movie.setDuration(request.duration());
        try {
            Movie savedMovie = movieRepository.save(movie);
            log.info("Movie created with id: {}", savedMovie.getId());
            return new MovieResponse(movie.getId(), movie.getName(), movie.getDescription());
        } catch (Exception e) {
            log.error("Error creating movie: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public List<MovieResponse> getAllMovies() {
        // TODO : Add Pagination + Caching
        return movieRepository.findAll().stream()
                .map(movie -> new MovieResponse(movie.getId(), movie.getName(), movie.getDescription()))
                .toList();
    }

    public MovieResponse getMovieById(String id) {
        return movieRepository.findById(id).map(movie -> new MovieResponse(movie.getId(), movie.getName(), movie.getDescription()))
                .orElseThrow(() -> new ResourceNotFoundException("Movie","Movie Id ",id));
    }

    public List<MovieResponse> findMoviesByCity(String city) {
        // Call theater service to get list of movie IDs
        List<String> movieIds = theaterRestClient.get()
                .uri("/api/v1/shows/internal/movie-ids?city={city}", city)
                .retrieve()
                .body(List.class);

        if(movieIds == null || movieIds.isEmpty()) {
            return Collections.emptyList();
        }
        log.info("Found {} movies for city: {}", movieIds.size(), city);
        return movieRepository.findAllById(movieIds).stream()
                .map(movie -> new MovieResponse(movie.getId(), movie.getName(), movie.getDescription()))
                .toList();
    }
}
