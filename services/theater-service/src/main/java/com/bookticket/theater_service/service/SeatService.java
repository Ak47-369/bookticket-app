package com.bookticket.theater_service.service;

import com.bookticket.theater_service.Entity.Screen;
import com.bookticket.theater_service.Entity.Seat;
import com.bookticket.theater_service.Entity.ShowSeat;
import com.bookticket.theater_service.dto.*;
import com.bookticket.theater_service.enums.SeatType;
import com.bookticket.theater_service.enums.ShowSeatStatus;
import com.bookticket.theater_service.repository.ScreenRepository;
import com.bookticket.theater_service.repository.SeatRepository;
import com.bookticket.theater_service.repository.ShowSeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
@Slf4j
public class SeatService {
    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;
    private final ShowSeatRepository showSeatRepository;

    public SeatService(SeatRepository seatRepository, ScreenRepository screenRepository, ShowSeatRepository showSeatRepository) {
        this.seatRepository = seatRepository;
        this.screenRepository = screenRepository;
        this.showSeatRepository = showSeatRepository;
    }

    public SeatResponse getSeatById(Long seatId) {
        return seatRepository.findById(seatId)
                .map(seat -> new SeatResponse(seat.getId(), seat.getRow(), seat.getColumn(), seat.getSeatType().name()))
                .orElseThrow(() -> new RuntimeException("Seat not found"));
    }

    public List<SeatTemplateResponse> getSeatTemplateByScreenId(Long screenId) {
        return seatRepository.findByScreenId(screenId).stream()
                .map(seat -> new SeatTemplateResponse(seat.getId(),seat.getSeatNumber(), seat.getSeatType().name(), seat.getPrice()))
                .toList();
    }

    public List<ValidSeatResponse> getAvailableSeats(Long showId, List<Long> seatIds) {
        List<ShowSeat> validSeats = showSeatRepository.findByShowIdAndShowSeatIdsAndStatus(showId, seatIds, ShowSeatStatus.AVAILABLE);
        return buildValidSeatResponse(validSeats);
    }

    public List<ValidSeatResponse> getSeatByShowAndSeatIds(Long showId, List<Long> seatIds) {
        List<ShowSeat>showSeats = showSeatRepository.findByShowIdAndShowSeatIds(showId, seatIds);
        log.info("Found {} seats", showSeats.size());
        if(showSeats.size() != seatIds.size()) {
            log.warn("Requested {} seats, but found only {} seats", seatIds.size(), showSeats.size());
        }
        return buildValidSeatResponse(showSeats);
    }

    @Transactional
    public List<SeatTemplateResponse> createSeatTemplate(Long screenId, List<CreateSeatTemplateRequest> createSeatTemplateRequests) {
        // Verify screen exists
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found with id: " + screenId));

        log.info("Creating {} seat templates for screen id: {}", createSeatTemplateRequests.size(), screenId);

        // Create seat entities from requests
        List<Seat> seats = createSeatTemplateRequests.stream()
                .map(request -> {
                    Seat seat = new Seat();
                    seat.setRow(request.row());
                    seat.setColumn(request.column());
                    seat.setSeatType(request.seatType());
                    seat.setPrice(request.price());
                    seat.setScreen(screen);
                    return seat;
                })
                .toList();

        // Bulk save all seats
        List<Seat> savedSeats = seatRepository.saveAll(seats);
        log.info("Successfully created {} seats for screen id: {}", savedSeats.size(), screenId);

        // Map to response
        return savedSeats.stream()
                .map(seat -> new SeatTemplateResponse(seat.getId(),seat.getSeatNumber(), seat.getSeatType().name(), seat.getPrice()))
                .toList();
    }

    @Transactional
    public SeatTemplateResponse updateSeatPrice(Long screenId, Long seatId, Double price) {
        Seat seat = seatRepository.findByScreenIdAndId(screenId, seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found with id: " + seatId + " for screen: " + screenId));

        int updated = seatRepository.updateSeatPrice(screenId, seatId, price);
        if (updated == 0) {
            throw new RuntimeException("Failed to update seat price");
        }

        log.info("Updated seat price for seat id: {} in screen id: {} to {}", seatId, screenId, price);
        seat.setPrice(price); // Update local object for response
        return new SeatTemplateResponse(seatId,seat.getSeatNumber(), seat.getSeatType().name(), price);
    }

    @Transactional
    public SeatTemplateResponse updateSeatType(Long screenId, Long seatId, String seatType) {
        Seat seat = seatRepository.findByScreenIdAndId(screenId, seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found with id: " + seatId + " for screen: " + screenId));

        SeatType seatTypeEnum;
        try {
            seatTypeEnum = SeatType.valueOf(seatType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid seat type: " + seatType + ". Valid values are: SILVER, GOLD, PLATINUM");
        }

        int updated = seatRepository.updateSeatType(screenId, seatId, seatTypeEnum);
        if (updated == 0) {
            throw new RuntimeException("Failed to update seat type");
        }

        log.info("Updated seat type for seat id: {} in screen id: {} to {}", seatId, screenId, seatType);
        seat.setSeatType(seatTypeEnum); // Update local object for response
        return new SeatTemplateResponse(seatId,seat.getSeatNumber(), seatTypeEnum.name(), seat.getPrice());
    }

    @Transactional
    public void deleteSeat(Long screenId, Long seatId) {
        Seat seat = seatRepository.findByScreenIdAndId(screenId, seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found with id: " + seatId + " for screen: " + screenId));

        seatRepository.deleteByScreenIdAndId(screenId, seatId);
        log.info("Deleted seat id: {} from screen id: {}", seatId, screenId);
    }

    @Transactional
    public List<SeatTemplateResponse> updateSeatTemplate(Long screenId, List<CreateSeatTemplateRequest> updateRequests) {
        // Verify screen exists
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found with id: " + screenId));

        log.info("Updating seat templates for screen id: {}", screenId);

        // Get all existing seats for this screen
        List<Seat> existingSeats = seatRepository.findByScreenId(screenId);

        // Create a map of existing seats by row-column for quick lookup
        var existingSeatMap = existingSeats.stream()
                .collect(Collectors.toMap(
                        seat -> seat.getRow() + "-" + seat.getColumn(),
                        seat -> seat
                ));

        // Process each update request
        List<Seat> seatsToSave = updateRequests.stream()
                .map(request -> {
                    String key = request.row() + "-" + request.column();
                    Seat seat = existingSeatMap.get(key);

                    if (seat != null) {
                        // Update existing seat
                        log.debug("Updating existing seat at row: {}, column: {}", request.row(), request.column());
                        seat.setSeatType(request.seatType());
                        seat.setPrice(request.price());
                    } else {
                        // Create new seat
                        log.debug("Creating new seat at row: {}, column: {}", request.row(), request.column());
                        seat = new Seat();
                        seat.setRow(request.row());
                        seat.setColumn(request.column());
                        seat.setSeatType(request.seatType());
                        seat.setPrice(request.price());
                        seat.setScreen(screen);
                    }
                    return seat;
                })
                .toList();

        // Bulk save all seats (both new and updated)
        List<Seat> savedSeats = seatRepository.saveAll(seatsToSave);
        log.info("Successfully updated/created {} seats for screen id: {}", savedSeats.size(), screenId);

        // Map to response
        return savedSeats.stream()
                .map(seat -> new SeatTemplateResponse(seat.getId(),seat.getSeatNumber(), seat.getSeatType().name(), seat.getPrice()))
                .toList();
    }

    @Transactional
    public List<ValidSeatResponse> lockSeatsByShowAndSeatIds(LockSeatsRequest lockSeatsRequest) {
       log.info("Locking seats for show id: {} and seat ids: {}",
                lockSeatsRequest.showId(),
                lockSeatsRequest.seatIds()
        );

       int updatedCount = showSeatRepository.updateShowSeatStatus(
                lockSeatsRequest.showId(),
                lockSeatsRequest.seatIds(),
                ShowSeatStatus.LOCKED
       );

       if(updatedCount != lockSeatsRequest.seatIds().size()) {
           log.warn("Failed to lock all seats. Requested: {}, Locked : {}, Rolling Back",
                   lockSeatsRequest.seatIds().size(),
                   updatedCount
           );
           // TODO - throws SeatsUnavailableException("Some Seats are not longer avaialble, Please try again."); 409 Conflict
            throw new RuntimeException("Failed to lock seats");
       }

       log.info("Successfully locked {} seats for show id: {}", updatedCount, lockSeatsRequest.showId());
       return getSeatByShowAndSeatIds(lockSeatsRequest.showId(), lockSeatsRequest.seatIds());
    }

    @Transactional
    public List<ValidSeatResponse> bookSeatsByShowAndSeatIds(BookSeatsRequest bookSeatsRequest) {
        log.info("Booking seats for show id: {} and seat ids: {}",
                bookSeatsRequest.showId(),
                bookSeatsRequest.seatIds()
        );

        int updatedCount = showSeatRepository.updateShowSeatStatus(
                bookSeatsRequest.showId(),
                bookSeatsRequest.seatIds(),
                ShowSeatStatus.BOOKED
        );

        if(updatedCount != bookSeatsRequest.seatIds().size()) {
            log.warn("Failed to book all seats. Requested: {}, Booked : {}, Rolling Back",
                    bookSeatsRequest.seatIds().size(),
                    updatedCount
            );
            throw new RuntimeException("Failed to book seats");
        }

        log.info("Successfully booked {} seats for show id: {}", updatedCount, bookSeatsRequest.showId());
        return getSeatByShowAndSeatIds(bookSeatsRequest.showId(), bookSeatsRequest.seatIds());
    }

    @Transactional
    public List<ValidSeatResponse> releaseSeatsByShowAndSeatIds(ReleaseSeatsRequest releaseSeatsRequest) {
        log.info("Releasing seats for show id: {} and seat ids: {}",
                releaseSeatsRequest.showId(),
                releaseSeatsRequest.showSeatIds()
        );

        int updatedCount = showSeatRepository.updateShowSeatStatus(
                releaseSeatsRequest.showId(),
                releaseSeatsRequest.showSeatIds(),
                ShowSeatStatus.AVAILABLE
        );

        if(updatedCount != releaseSeatsRequest.showSeatIds().size()) {
            log.warn("Failed to release all seats. Requested: {}, Released : {}, Rolling Back",
                    releaseSeatsRequest.showSeatIds().size(),
                    updatedCount
            );
//            throw new RuntimeException("Failed to release seats");
        }

        log.info("Successfully released {} seats for show id: {}", updatedCount, releaseSeatsRequest.showId());
        return getSeatByShowAndSeatIds(releaseSeatsRequest.showId(), releaseSeatsRequest.showSeatIds());
    }

    private List<ValidSeatResponse> buildValidSeatResponse(List<ShowSeat> showSeats) {
        return showSeats.stream()
                .map(showSeat -> new ValidSeatResponse(
                        showSeat.getId(),
                        showSeat.getSeat().getSeatNumber(),
                        showSeat.getSeat().getSeatType().name(),
                        showSeat.getPrice()
                ))
                .toList();
    }
}
