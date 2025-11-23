package com.bookticket.theater_service.Entity;

import com.bookticket.theater_service.enums.ShowSeatStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "show_seats")
@Data
public class ShowSeat extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_seat_id")
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private ShowSeatStatus status;

    @Column(nullable = false)
    private double price; // Copied from Seat, can be changed by dynamic pricing
}
