package com.bookticket.theater_service.repository;

import com.bookticket.theater_service.Entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScreenRepository extends JpaRepository<Screen,Long> {
    Optional<List<Screen>> findByTheaterId(Long theaterId);
}
