package com.bookticket.theater_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
@Data
public class Show extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_id")
    private Long id;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen; // The screen where it's playing
    @Column(nullable = false)
    private String movieId;
    // Denormalization
    @Column(nullable = false)
    private String movieTitle;
    private String movieBannerUrl;
}
