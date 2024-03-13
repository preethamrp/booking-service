package com.maersk.bookingservice.service;

import com.maersk.bookingservice.entity.Booking;
import com.maersk.bookingservice.exception.InternalException;
import com.maersk.bookingservice.model.BookingDto;
import com.maersk.bookingservice.model.BookingResponse;
import com.maersk.bookingservice.model.ContainerType;
import com.maersk.bookingservice.repository.BookingRepository;
import com.maersk.bookingservice.util.ConversionUtils;
import com.mongodb.MongoClientException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

    private BookingService bookingService;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, sequenceGeneratorService);
        bookingDto = new BookingDto();
        bookingDto = new BookingDto();
        bookingDto.setContainerSize(20);
        bookingDto.setDestination("USA");
        bookingDto.setOrigin("India");
        bookingDto.setContainerType(ContainerType.DRY);
        bookingDto.setQuantity(5);
        bookingDto.setTimestamp(Instant.now());
    }

    @Test
    void saveBooking_Success() {
        Booking bookingEntity = ConversionUtils.dtoToEntity(bookingDto);
        bookingEntity.setId(966383);
        long bookingId = 966383L;
        BookingResponse expectedResponse = new BookingResponse(String.valueOf(bookingId), null);

        when(sequenceGeneratorService.generateSequence(anyString())).thenReturn(bookingId);
        when(bookingRepository.insert(any(Booking.class))).thenReturn(Mono.just(bookingEntity));

        // Call the method and verify response
        Mono<BookingResponse> result = bookingService.saveBooking(Mono.just(bookingDto));
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        // Verify interactions
        verify(sequenceGeneratorService).generateSequence(anyString());
        verify(bookingRepository).insert(any(Booking.class));
    }

    @Test
    void saveBooking_Retry() {
        Booking bookingEntity = ConversionUtils.dtoToEntity(bookingDto);
        bookingEntity.setId(966383);
        long bookingId = 966383L;
        when(sequenceGeneratorService.generateSequence(anyString())).thenReturn(bookingId);

        when(bookingRepository.insert(any(Booking.class))).thenThrow(new MongoException("Some Exception"));

        // Call the method and verify response
        Mono<BookingResponse> result = bookingService.saveBooking(Mono.just(bookingDto));
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof InternalException )
                .verify();

        // Verify interactions
        verify(sequenceGeneratorService, Mockito.times(4)).generateSequence(anyString());
        verify(bookingRepository, Mockito.times(4)).insert(any(Booking.class));


    }

}