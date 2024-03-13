package com.maersk.bookingservice.controller;

import com.maersk.bookingservice.exception.DatabaseException;
import com.maersk.bookingservice.exception.InternalException;
import com.maersk.bookingservice.model.AvailabilityDto;
import com.maersk.bookingservice.model.AvailabilityResponse;
import com.maersk.bookingservice.model.BookingDto;
import com.maersk.bookingservice.model.BookingResponse;
import com.maersk.bookingservice.service.AvailabilityService;
import com.maersk.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/api/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;
    private final AvailabilityService availabilityService;

    public BookingController(BookingService bookingService, AvailabilityService availabilityService) {
        this.bookingService = bookingService;
        this.availabilityService = availabilityService;
    }

    @PostMapping("/book")
    public Mono<ResponseEntity<BookingResponse>> bookContainer(@Valid @RequestBody Mono<BookingDto> booking) {

        return bookingService.saveBooking(booking)
                .map(savedBooking -> ResponseEntity.status(HttpStatus.CREATED).body(savedBooking))
                .onErrorResume(WebExchangeBindException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new BookingResponse(null, Objects.requireNonNull(ex.getFieldError()).getDefaultMessage()))))
                .onErrorResume(InternalException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new BookingResponse(null, ex.getMessage()))))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));


    }


    @PostMapping("/checkAvailability")
    public Mono<ResponseEntity<AvailabilityResponse>> checkAvailability(@Valid @RequestBody Mono<AvailabilityDto> booking) {

        return availabilityService.checkAvailability(booking)
                .map(savedBooking -> ResponseEntity.status(HttpStatus.OK).body(savedBooking))
                .onErrorResume(WebExchangeBindException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new AvailabilityResponse(null, Objects.requireNonNull(ex.getFieldError()).getDefaultMessage()))))
                .onErrorResume(InternalException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new AvailabilityResponse(null, ex.getMessage()))))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));


    }



}
