package com.maersk.bookingservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.maersk.bookingservice.model.AvailabilityDto;
import com.maersk.bookingservice.model.AvailabilityResponse;
import com.maersk.bookingservice.model.BookingDto;
import com.maersk.bookingservice.model.BookingResponse;
import com.maersk.bookingservice.service.AvailabilityService;
import com.maersk.bookingservice.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private AvailabilityService availabilityService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testBookContainer_ValidBooking() throws Exception {
        String request = """
                {
                    "containerType": "DRY",
                    "containerSize": 20,
                    "origin": "USAAA",
                    "destination": "Singapore",
                    "quantity": 5,
                    "timestamp": "2020-10-12T13:53:09Z"
                }
                """;

        String response = """
                {
                    "bookingRef": "957000007"
                }
                """;

        BookingDto bookingDto = objectMapper.readValue(request, BookingDto.class);
        BookingResponse bookingResponse = objectMapper.readValue(response, BookingResponse.class);

        when(bookingService.saveBooking(any())).thenReturn(Mono.just(bookingResponse));


        WebTestClient.ResponseSpec response1 = webTestClient.post().uri("/api/bookings/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(bookingDto), BookingDto.class)
                .exchange();


        response1.expectStatus().isCreated()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.bookingRef").isEqualTo("957000007");

    }

    @Test
    void checkAvailability_Success() throws JsonProcessingException {
        // Mock data

        String request = """
                {
                    "containerType": "DRY",
                    "containerSize": 20,
                    "origin": "USAAA",
                    "destination": "Singapore",
                    "quantity": 5
                }
                """;

        String response = """
                {
                    "available": true
                }
                """;

        AvailabilityDto availabilityDto = objectMapper.readValue(request, AvailabilityDto.class);
        AvailabilityResponse expectedResponse = objectMapper.readValue(response, AvailabilityResponse.class);


        // Mock interactions
        when(availabilityService.checkAvailability(any(Mono.class))).thenReturn(Mono.just(expectedResponse));

        WebTestClient.ResponseSpec response1 = webTestClient.post().uri("/api/bookings/checkAvailability")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(availabilityDto), AvailabilityDto.class)
                .exchange();


        response1.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.available").isEqualTo(true);


    }


}