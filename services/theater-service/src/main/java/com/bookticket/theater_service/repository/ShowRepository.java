package com.bookticket.theater_service.repository;

import com.bookticket.theater_service.Entity.Show;
import com.bookticket.theater_service.dto.ShowResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowRepository extends JpaRepository<Show,Long> {
    // Query to find distinct movie IDs by city for upcoming shows
    @Query("SELECT DISTINCT s.movieId FROM Show s " +
            "JOIN s.screen sc " +
            "JOIN sc.theater t " +
            "WHERE t.city = :city AND s.startTime > CURRENT_TIMESTAMP")
    List<String> findDistinctMovieIdsByCity(@Param("city") String city);

    @Query("SELECT new com.bookticket.theater_service.dto.ShowResponse(" +
            "s.id, s.startTime, s.endTime, s.movieId, " +
            "s.movieTitle, s.movieBannerUrl, " +
            "t.name, t.address, sc.name) " +
            "FROM Show s " +
            "JOIN s.screen sc " +
            "JOIN sc.theater t " +
            "WHERE (:city IS NULL OR t.city = :city) " +
            "AND (:theaterId IS NULL OR t.id = :theaterId) " +
            "AND (:movieId IS NULL OR s.movieId = :movieId) " +
            "AND s.startTime >= :startOfDay " +
            "AND s.startTime < :endOfDay")
    List<ShowResponse> findShowsByFilters(
            @Param("city") String city,
            @Param("theaterId") Long theaterId,
            @Param("movieId") String movieId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
    

    @Query("SELECT s FROM Show s WHERE s.id = :showId")
    Show findShowById(@Param("showId") Long showId);

    @Query("SELECT s FROM Show s WHERE s.screen.id = :screenId")
    List<Show> findShowsByScreenId(@Param("screenId") Long screenId);

}
