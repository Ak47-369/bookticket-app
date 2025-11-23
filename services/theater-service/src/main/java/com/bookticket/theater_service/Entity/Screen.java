package com.bookticket.theater_service.Entity;

import com.bookticket.theater_service.enums.ScreenType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "screens")
@Getter
@Setter
public class Screen extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_id")
    private Long id;
    private String name;
    private int rows;
    private int columns;
    @Enumerated(EnumType.STRING)
    private ScreenType screenType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    public int getTotalSeats() {
        return rows * columns;
    }
}
