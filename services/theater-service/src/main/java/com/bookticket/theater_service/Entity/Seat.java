package com.bookticket.theater_service.Entity;

import com.bookticket.theater_service.enums.SeatType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seat_row", "seat_column", "screen_id"})
})
@Getter
@Setter
public class Seat extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;
    @Column(name = "seat_row", nullable = false)
    private int row;
    @Column(name = "seat_column", nullable = false)
    private int column;
    @Enumerated(EnumType.STRING)
    private SeatType seatType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;
    private double price; // Base price

    public String getSeatNumber() {
        return row + "-" + column;
    }
}
