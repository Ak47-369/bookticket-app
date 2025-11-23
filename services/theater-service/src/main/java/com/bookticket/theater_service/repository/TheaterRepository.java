package com.bookticket.theater_service.repository;

import com.bookticket.theater_service.Entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<Theater,Long> {
}
