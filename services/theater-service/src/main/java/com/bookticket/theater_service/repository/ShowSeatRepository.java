package com.bookticket.theater_service.repository;

import com.bookticket.theater_service.Entity.ShowSeat;
import com.bookticket.theater_service.dto.ShowSeatResponse;
import com.bookticket.theater_service.enums.ShowSeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat,Long> {
    // Get Seat Map API
    @Query("SELECT new com.bookticket.theater_service.dto.ShowSeatResponse(" +
            "ss.id, s.row || '-' || s.column, s.seatType, ss.price, ss.status) " +
            "FROM ShowSeat ss " +
            "JOIN ss.seat s " +
            "WHERE ss.show.id = :showId " +
            "ORDER BY s.row, s.column") // Order by row and then column
    List<ShowSeatResponse> findShowSeatsByShowId(@Param("showId") Long showId);

    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.id IN :showSeatIds")
    List<ShowSeat> findByShowIdAndShowSeatIds(@Param("showId") Long showId,
                                              @Param("showSeatIds") List<Long> showSeatIds
    );

    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.id IN :showSeatIds AND ss.status = :status")
    List<ShowSeat> findByShowIdAndShowSeatIdsAndStatus(@Param("showId") Long showId,
                                                       @Param("showSeatIds") List<Long> showSeatIds,
                                                       @Param("status") ShowSeatStatus status
    );

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ShowSeat ss SET ss.status = :status " +
            "WHERE ss.show.id = :showId " +
            "AND ss.id IN :showSeatIds " +
            "AND ( " +
            "  (:status = 'LOCKED' AND ss.status = 'AVAILABLE') OR " + // Lock only available Seats
            "  (:status <> 'LOCKED' AND ss.status = 'LOCKED')" + // And book only locked seats
            ")")
    int updateShowSeatStatus(@Param("showId") Long showId,
                             @Param("showSeatIds") List<Long> showSeatIds,
                             @Param("status") ShowSeatStatus status
    );
}
