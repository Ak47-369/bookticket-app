package com.bookticket.theater_service.service;


import com.bookticket.theater_service.Entity.Screen;
import com.bookticket.theater_service.Entity.Seat;
import com.bookticket.theater_service.Entity.Show;
import com.bookticket.theater_service.Entity.ShowSeat;
import com.bookticket.theater_service.dto.CreateShowRequest;
import com.bookticket.theater_service.dto.MovieResponse;
import com.bookticket.theater_service.dto.ShowResponse;
import com.bookticket.theater_service.dto.ShowSeatResponse;
import com.bookticket.theater_service.enums.ShowSeatStatus;
import com.bookticket.theater_service.repository.ScreenRepository;
import com.bookticket.theater_service.repository.SeatRepository;
import com.bookticket.theater_service.repository.ShowRepository;
import com.bookticket.theater_service.repository.ShowSeatRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShowService {
    private final ShowRepository showRepository;
    private final ScreenRepository screenRepository;
    private final RestClient movieRestClient;
    private final ShowSeatRepository showSeatRepository;
    private final SeatRepository seatRepository;

    public ShowService(ShowRepository showRepository, ScreenRepository screenRepository, RestClient movieRestClient,
                       ShowSeatRepository showSeatRepository, SeatRepository seatRepository) {
        this.showRepository = showRepository;
        this.screenRepository = screenRepository;
        this.movieRestClient = movieRestClient;
        this.showSeatRepository = showSeatRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public ShowResponse createShow(Long screenId,CreateShowRequest createShowRequest) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found"));
        log.info("Screen found: {}", screen);
        // TODO : Make it Async, This is a sync service call, Should i use .block() ?
        MovieResponse movieResponse = movieRestClient.get()
                .uri("/api/v1/movies/{movieId}", createShowRequest.movieId())
                .retrieve()
                .body(MovieResponse.class);
        if(movieResponse == null) {
            throw new RuntimeException("Movie not found with id : "+ createShowRequest.movieId());
        }

        Show newShow = new Show();
        newShow.setStartTime(createShowRequest.startTime());
        newShow.setEndTime(createShowRequest.endTime());
        newShow.setMovieId(createShowRequest.movieId());
        newShow.setMovieTitle(movieResponse.title());
        newShow.setMovieBannerUrl(movieResponse.bannerUrl());
        newShow.setScreen(screen);
        Show savedShow = null;
        try {
            savedShow = showRepository.save(newShow);
            log.info("Show created successfully");
        } catch (Exception e) {
            log.error("Error creating show", e);
            throw new RuntimeException(e);
        }

        // Populate ShowSeat Inventory
        List<Seat> templateSeats = seatRepository.findByScreenId(screenId);
        Show finalSavedShow = savedShow;
        List<ShowSeat> showSeats = templateSeats.stream()
                .map(seat -> {
                    ShowSeat showSeat = new ShowSeat();
                    showSeat.setShow(finalSavedShow);
                    showSeat.setSeat(seat);
                    showSeat.setStatus(ShowSeatStatus.AVAILABLE);
                    showSeat.setPrice(seat.getPrice());
                    return showSeat;
                })
                .toList();

        try {
            showSeatRepository.saveAll(showSeats);
            log.info("ShowSeat created successfully");
        } catch (Exception e) {
            log.error("Error creating showSeat", e);
            throw new RuntimeException(e);
        }

        log.info("Show created successfully");
        return new ShowResponse(
                newShow.getId(),
                newShow.getStartTime(),
                newShow.getEndTime(),
                newShow.getMovieId(),
                newShow.getMovieTitle(),
                newShow.getMovieBannerUrl(),
                newShow.getScreen().getTheater().getName(),
                newShow.getScreen().getTheater().getAddress(),
                newShow.getScreen().getName()
        );

    }

    public List<String> getMovieIdsByCity(String city) {
        return showRepository.findDistinctMovieIdsByCity(city);
    }

    public List<ShowResponse> getShowsByFilters(String city, Long theaterId, String movieId, LocalDate date) {
        LocalDateTime startOfDay;
        LocalDateTime endOfDay;

        if (date != null) {
            // Filter by specific date
            startOfDay = date.atStartOfDay(); // 00:00:00
            endOfDay = date.plusDays(1).atStartOfDay(); // Next day 00:00:00
        } else {
            // Filter by current and upcoming shows for next 15 days
            startOfDay = LocalDateTime.now();
            endOfDay = startOfDay.plusDays(15);
        }

        log.info("Fetching shows with filters - city: {}, theaterId: {}, movieId: {}, date: {}",
                city, theaterId, movieId, date);
        log.info("Date range: startOfDay={}, endOfDay={}", startOfDay, endOfDay);

        List<ShowResponse> results = showRepository.findShowsByFilters(city, theaterId, movieId, startOfDay, endOfDay);
        log.info("Found {} shows matching the filters", results.size());

        return results;
    }

    // Backward compatibility method
    @Deprecated
    public List<ShowResponse> getShowsByMovieAndCityAndDate(String movieId, String city, LocalDate date) {
        return getShowsByFilters(city, null, movieId, date);
    }

    public List<ShowSeatResponse> getShowSeatsByShowId(Long showId) {
        return showSeatRepository.findShowSeatsByShowId(showId);
    }

    public ShowResponse getShowById(Long showId) {
        return showRepository.findById(showId)
                .map(show -> new ShowResponse(
                        show.getId(),
                        show.getStartTime(),
                        show.getEndTime(),
                        show.getMovieId(),
                        show.getMovieTitle(),
                        show.getMovieBannerUrl(),
                        show.getScreen().getTheater().getName(),
                        show.getScreen().getTheater().getAddress(),
                        show.getScreen().getName()
                ))
                .orElseThrow(() -> new RuntimeException("Show not found"));
    }

    public List<ShowResponse> getShowsByScreenId(Long screenId) {
        return showRepository.findShowsByScreenId(screenId).stream()
                .map(show -> new ShowResponse(
                        show.getId(),
                        show.getStartTime(),
                        show.getEndTime(),
                        show.getMovieId(),
                        show.getMovieTitle(),
                        show.getMovieBannerUrl(),
                        show.getScreen().getTheater().getName(),
                        show.getScreen().getTheater().getAddress(),
                        show.getScreen().getName()
                ))
                .toList();
    }
}
