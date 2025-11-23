package com.bookticket.theater_service.controller;

import com.bookticket.theater_service.dto.CreateTheaterRequest;
import com.bookticket.theater_service.service.TheaterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * We use @WebMvcTest on the *controller* we want to test.
 * This loads your SecurityConfig, your controller, and all web-layer components.
 * It does NOT load your @Service or @Repository beans.
 */
@WebMvcTest(TheaterController.class)
public class TheaterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private TheaterService theaterService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateTheater() throws Exception {
        // Arrange: Create a request payload and stub the service method
        CreateTheaterRequest request = new CreateTheaterRequest("Grand Cinema", "123 Main St", "Springfield", "IL", "62704", null);
        when(theaterService.createTheater(any(CreateTheaterRequest.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/theaters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verify that the service method was called exactly once
        verify(theaterService).createTheater(any(CreateTheaterRequest.class));
    }



}
