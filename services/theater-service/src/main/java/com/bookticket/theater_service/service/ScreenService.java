package com.bookticket.theater_service.service;

import com.bookticket.theater_service.Entity.Screen;
import com.bookticket.theater_service.Entity.Theater;
import com.bookticket.theater_service.dto.CreateScreenRequest;
import com.bookticket.theater_service.dto.ScreenResponse;
import com.bookticket.theater_service.dto.UpdateScreenRequest;
import com.bookticket.theater_service.repository.ScreenRepository;
import com.bookticket.theater_service.util.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ScreenService {
    private final ScreenRepository screenRepository;

    public ScreenService(ScreenRepository screenRepository) {
        this.screenRepository = screenRepository;
    }

    public ScreenResponse createScreen(Long theaterId,CreateScreenRequest createScreenRequest) {
        try{
            Screen newScreen = new Screen();
            newScreen.setName(createScreenRequest.name());
            newScreen.setRows(createScreenRequest.rows());
            newScreen.setColumns(createScreenRequest.columns());
            newScreen.setTheater(new Theater());
            newScreen.getTheater().setId(theaterId);
            newScreen.setScreenType(createScreenRequest.screenType());
            Screen createdScreen = screenRepository.save(newScreen);
            log.info("Screen created successfully {}", createdScreen);
            return new ScreenResponse(createdScreen.getId(),
                    createdScreen.getName(),
                    createdScreen.getRows(),
                    createdScreen.getColumns(),
                    createdScreen.getScreenType()
            );
        } catch (Exception e) {
            log.error("Error creating screen", e);
            throw new RuntimeException(e);
        }
    }

    public List<ScreenResponse> getScreensByTheaterId(Long theaterId) {
        log.info("Getting screens for theater id {}", theaterId);
        List<Screen> screens = screenRepository.findByTheaterId(theaterId)
                .orElseThrow(() -> new RuntimeException("Screens not found"));
        return screens.stream()
                .map(screen -> new ScreenResponse(screen.getId(),
                        screen.getName(),
                        screen.getRows(),
                        screen.getColumns(),
                        screen.getScreenType()))
                .toList();
    }

    public ScreenResponse getScreenById(Long screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found"));
        return new ScreenResponse(screen.getId(),
                screen.getName(),
                screen.getRows(),
                screen.getColumns(),
                screen.getScreenType()
        );
    }

    public ScreenResponse updateScreen(Long screenId, UpdateScreenRequest updateScreenRequest) {
        try{
            Screen screen = screenRepository.findById(screenId)
                    .orElseThrow(() -> new RuntimeException("Screen not found"));
            UpdateUtil.updateIfNotEmpty(updateScreenRequest.name(), screen::setName);
            UpdateUtil.updateIfPositive(updateScreenRequest.rows(), screen::setRows);
            UpdateUtil.updateIfPositive(updateScreenRequest.columns(), screen::setColumns);
            UpdateUtil.updateIfNotNull(updateScreenRequest.screenType(), screen::setScreenType);
            Screen updatedScreen = screenRepository.save(screen);
            log.info("Screen updated successfully {}", updatedScreen);
            return new ScreenResponse(updatedScreen.getId(),
                    updatedScreen.getName(),
                    updatedScreen.getRows(),
                    updatedScreen.getColumns(),
                    updatedScreen.getScreenType()
            );
        } catch (Exception e) {
            log.error("Error updating screen", e);
            throw new RuntimeException(e);
        }
    }

    public void deleteScreen(Long screenId) {
        screenRepository.deleteById(screenId);
    }
}
