package com.maersk.bookingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.maersk.bookingservice.model.BookingDto;
import com.maersk.bookingservice.model.BookingResponse;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookingService bookingService;

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
    void testBookContainer_InvalidBooking_containerSize() throws Exception {

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
        BookingDto bookingDto = objectMapper.readValue(request, BookingDto.class);
        String errorMessage = "Invalid booking data";

        when(bookingService.saveBooking(any())).thenThrow(new WebExchangeBindException(null, mock(BindingResult.class)));

        WebTestClient.ResponseSpec response1 = webTestClient.post().uri("/api/bookings/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(bookingDto), BookingDto.class)
                .exchange();


        response1.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.error").isEqualTo(errorMessage);


    }


}