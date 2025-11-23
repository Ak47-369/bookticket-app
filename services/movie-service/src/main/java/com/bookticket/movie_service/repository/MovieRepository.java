package com.bookticket.movie_service.repository;

import com.bookticket.movie_service.entity.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MovieRepository extends MongoRepository<Movie, String> {
    Optional<Movie> findByName(String name);
}
