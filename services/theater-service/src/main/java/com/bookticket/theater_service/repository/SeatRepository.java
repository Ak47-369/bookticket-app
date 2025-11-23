package com.bookticket.theater_service.repository;

import com.bookticket.theater_service.Entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat,Long> {
    List<Seat> findByScreenId(Long screenId);

    Optional<Seat> findByScreenIdAndId(Long screenId, Long seatId);

    @Modifying
    @Query("UPDATE Seat s SET s.price = :price WHERE s.screen.id = :screenId AND s.id = :seatId")
    int updateSeatPrice(@Param("screenId") Long screenId, @Param("seatId") Long seatId, @Param("price") Double price);

    @Modifying
    @Query("UPDATE Seat s SET s.seatType = :seatType WHERE s.screen.id = :screenId AND s.id = :seatId")
    int updateSeatType(@Param("screenId") Long screenId, @Param("seatId") Long seatId, @Param("seatType") com.bookticket.theater_service.enums.SeatType seatType);

    void deleteByScreenIdAndId(Long screenId, Long seatId);
}
